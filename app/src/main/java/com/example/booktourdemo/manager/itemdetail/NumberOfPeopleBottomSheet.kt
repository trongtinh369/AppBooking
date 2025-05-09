package com.example.booktourdemo.manager.itemdetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.booktourdemo.databinding.BottomSheetNumberOfPeopleBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
// cái này cho trang Itemdetail khi nhập so nguoi di tuor

class NumberOfPeopleBottomSheet : BottomSheetDialogFragment() {

    // Callback để gửi dữ liệu số người về Activity/Fragment
    private var onConfirmListener: ((Int) -> Unit)? = null

    private var _binding: BottomSheetNumberOfPeopleBinding? = null
    private val binding get() = _binding!!

    fun setOnConfirmListener(listener: (Int) -> Unit) {
        this.onConfirmListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Nạp layout của bottom sheet bằng View Binding
        _binding = BottomSheetNumberOfPeopleBinding.inflate(inflater, container, false)

        // Xử lý nút Xác nhận
        binding.buttonConfirm.setOnClickListener {
            val numberOfPeople = binding.editNumberOfPeople.text.toString().toIntOrNull()


            if (numberOfPeople != null && numberOfPeople in 1..5) {
                onConfirmListener?.invoke(numberOfPeople) // nó gọi callback đó và truyền giá trị vào.
                dismiss() // Đóng bottom sheet
            } else {
                binding.editNumberOfPeople.error = "Vui lòng nhập số từ 1 đến 4"
            }
        }

        // Xử lý nút Hủy
        binding.buttonCancel.setOnClickListener {
            dismiss() // Đóng bottom sheet
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Tránh rò rỉ bộ nhớ
    }

}