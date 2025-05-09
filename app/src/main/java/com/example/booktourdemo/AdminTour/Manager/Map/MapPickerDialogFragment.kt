package com.example.booktourdemo.AdminTour.Manager.Map

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.booktourdemo.R
import com.example.booktourdemo.models.Tour.Location
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class MapPickerDialogFragment(
    private val onLocationPicked: (location: Location) -> Unit
) : DialogFragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private val LOCATION_PERMISSION_REQUEST_CODE = 100
    private var selectedLatLng: LatLng? = null // Lưu vị trí được click
    private lateinit var btnConfirm: Button // Nút Xác nhận

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_map_picker)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        // Tham chiếu đến nút Xác nhận
        btnConfirm = dialog.findViewById(R.id.btnConfirm)

        // Kiểm tra quyền truy cập vị trí
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            initializeMap()
        }
        return dialog
    }

    private fun initializeMap() {
        val mapFragment = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction().replace(R.id.mapFragment, mapFragment).commit()
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        Toast.makeText(requireContext(), "Map ready", Toast.LENGTH_SHORT).show()

        // Đặt vị trí mặc định là trung tâm Việt Nam
        val vietnamCenter = LatLng(16.047079, 108.206230) // Tọa độ trung tâm Việt Nam
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(vietnamCenter, 5f)) // Zoom mức 5 để thấy toàn Việt Nam

        // Xử lý click trên bản đồ
        googleMap.setOnMapClickListener { latLng ->
            selectedLatLng = latLng // Lưu vị trí được click
            googleMap.clear() // Xóa marker cũ
            googleMap.addMarker(
                MarkerOptions().position(latLng).title("Vị trí đã chọn")
            )
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f)) // Zoom vào vị trí
            btnConfirm.isEnabled = true // Kích hoạt nút Xác nhận
        }

        // Xử lý nhấn nút Xác nhận
        btnConfirm.setOnClickListener {
            selectedLatLng?.let { latLng ->
                val location = getLocationFromLatLng(latLng.latitude, latLng.longitude)
                if (location != null) {
                    onLocationPicked(location)
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "Không lấy được địa chỉ!", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(requireContext(), "Vui lòng chọn một vị trí!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getLocationFromLatLng(lat: Double, lng: Double): Location? {
        return try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                Location(
                    city = address.locality ?: "",
                    country = address.countryName ?: "",
                    latitude = lat,
                    longitude = lng
                )
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeMap()
            } else {
                Toast.makeText(requireContext(), "Cần quyền vị trí để hiển thị bản đồ!", Toast.LENGTH_LONG).show()
                dismiss()
            }
        }
    }
}