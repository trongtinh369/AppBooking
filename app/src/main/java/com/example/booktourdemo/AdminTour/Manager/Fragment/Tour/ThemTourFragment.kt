package com.example.booktourdemo.AdminTour.Manager.Fragment.Tour

import ImgBBAPI
import ImgBBResponse
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.booktourdemo.R
import com.example.booktourdemo.AdminTour.Manager.Map.MapPickerDialogFragment
import com.example.booktourdemo.databinding.ThemTourLayoutBinding
import com.example.booktourdemo.DatabaseAPI
import com.example.booktourdemo.firebase.OnDatabaseCallback
import com.example.booktourdemo.models.Tour.Location
import com.example.booktourdemo.models.Tour.Schedule
import com.example.booktourdemo.models.Tour.Statistics
import com.example.booktourdemo.models.Tour.Tour
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ThemTourFragment : Fragment() {

    private lateinit var imgbbAPI: ImgBBAPI
    private val imgbbKey = "3572ad4c638013b5996cc6ae4de38117"
    private var linkAnhDaChon: String? = null
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private var selectedImageUri: Uri? = null

    private lateinit var binding: ThemTourLayoutBinding
    private lateinit var databaseAPI: DatabaseAPI
    private lateinit var tourList: ArrayList<Tour>
    private lateinit var scheduleList: ArrayList<Schedule>
    private var selectedLocation: Location? = null

    private var imageList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = ThemTourLayoutBinding.inflate(inflater, container, false)

        getImage()

        // Initialize objects
        databaseAPI = DatabaseAPI(requireContext())
        tourList = ArrayList()
        scheduleList = ArrayList()

        // Load tours
        docTatCaTour()

        // Set event listeners
        setEvent()

        return binding.root
    }

    private fun setEvent() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnLuuThongTin.setOnClickListener {
            luuThongTin()
        }

        binding.btnChonAnh.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.btnGoogleMap.setOnClickListener {
            val dialog = MapPickerDialogFragment { location ->
                selectedLocation = location
                binding.tvThanhPho.text = location.city
            }
            dialog.show(parentFragmentManager, "MapPickerDialog")
        }
    }

    private fun getImage() {
        // lấ ảnh từ điện thoại
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                // Hiển thị ảnh vào ImageView bằng URI
                binding.imgTour.setImageURI(uri)
                // Lưu URI tạm thời để sử dụng khi upload sau
                selectedImageUri = uri
            }
        }
    }

    // Cần lệnh callback
    private fun uploadImageToImgBB(uri: Uri, callback: (String?) -> Unit) {

        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()

        if (bytes != null) {
            // Khởi tạo retrofit
            val retrofit = Retrofit.Builder()
                .baseUrl(ImgBBAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            // Ép lệnh và yêu cầu quyền
            imgbbAPI = retrofit.create(ImgBBAPI::class.java)

            //đóng gói dữ liệu ảnh gửi lên server
            val requestBody = RequestBody.create(
                "image/*".toMediaTypeOrNull(), bytes)

            val multipart = MultipartBody.Part.createFormData(
                "image", "upload.jpg", requestBody)

            // Gửi yêu cầu
            val call = imgbbAPI.uploadImage(imgbbKey, multipart)

            call.enqueue(object : retrofit2.Callback<ImgBBResponse> {
                override fun onResponse(call: Call<ImgBBResponse>, response: Response<ImgBBResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val imageUrl = response.body()?.data?.url
                        callback(imageUrl)
                    } else {
                        callback(null)
                        Toast.makeText(requireContext(), "Upload thất bại", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<ImgBBResponse>, t: Throwable) {
                    callback(null)
                    Toast.makeText(requireContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            callback(null)
        }
    }


    // gọi API trả về URL ảnh
    private fun luuThongTin() {
        // Lấy dữ liệu từ các trường
        val idTour = binding.edtIdTour.text.toString().trim()
        val tenTour = binding.edtTenTour.text.toString().trim()
        val moTa = binding.edtMoTa.text.toString().trim()
        val giaTour = binding.edtGiaTour.text.toString().trim()
        val soNgayTour = binding.edtSoNgayTour.text.toString().trim()

        // Kiểm tra các trường bắt buộc
        val errors = mutableListOf<String>()
        if (idTour.isEmpty()) errors.add("ID tour")
        if (tenTour.isEmpty()) errors.add("Tên tour")
        if (moTa.isEmpty()) errors.add("Mô tả tour")
        if (giaTour.isEmpty()) errors.add("Giá tour")
        if (soNgayTour.isEmpty()) errors.add("Số ngày tour")
        if (selectedLocation == null || selectedLocation?.city.isNullOrEmpty()) errors.add("Địa điểm tour")
        if (selectedImageUri == null) errors.add("Ảnh tour")

        // Nếu có lỗi, hiển thị một Toast duy nhất
        if (errors.isNotEmpty()) {
            val errorMessage = "Vui lòng nhập đủ thông tin: ${errors.joinToString(", ")}"
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
            return
        }

        // Nếu tất cả trường hợp lệ, tiến hành upload ảnh và lưu tour
        uploadImageToImgBB(selectedImageUri!!) { imageUrl ->
            if (imageUrl != null) {
                linkAnhDaChon = imageUrl
                val tour = layThongTin()
                databaseAPI.themTour(tour, object : OnDatabaseCallback {
                    override fun onSuccess(temp: Any) {
                        Toast.makeText(requireContext(), "Thêm thành công!", Toast.LENGTH_SHORT).show()
                        val navController = findNavController()
                        navController.navigate(R.id.action_themtours_to_trangChu)
                    }
                    override fun onFailure(e: Exception) {
                        Toast.makeText(requireContext(), "Thêm thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(requireContext(), "Không thể tải ảnh lên", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Sau khi có URL ảnh bỏ vào đối tượng
    private fun layThongTin(): Tour {
        val images = listOfNotNull(linkAnhDaChon)

        val tourLocation = Location(
            name = binding.edtIdTour.text.toString(),
            country = selectedLocation?.country ?: "",
            city = selectedLocation?.city ?: "",
            latitude = selectedLocation?.latitude ?: 0.0,
            longitude = selectedLocation?.longitude ?: 0.0
        )

        return Tour(
            id = binding.edtIdTour.text.toString(),
            title = binding.edtTenTour.text.toString(),
            description = binding.edtMoTa.text.toString(),
            location = tourLocation,
            price = binding.edtGiaTour.text.toString().toDoubleOrNull() ?: 0.0,
            duration = binding.edtSoNgayTour.text.toString().toIntOrNull() ?: 0,
            created_at = System.currentTimeMillis(),
            images = images,
            statistics = Statistics(booking_count = 0)
        )
    }


    private fun docTatCaTour() {
        databaseAPI.docTatCaTours(tourList, object : OnDatabaseCallback {
            override fun onSuccess(temp: Any) {}
            override fun onFailure(e: Exception) {}
        })
    }
}
