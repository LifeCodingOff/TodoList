package com.example.todolist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.todolist.databinding.FragmentSginUpBinding


class SginUpFragment : Fragment() {
    lateinit var binding : FragmentSginUpBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSginUpBinding.inflate(inflater, container, false)

        binding.sginUpBtn.setOnClickListener {
            // 이메일, 비밀번호 회원가입
            val email : String = binding.sginUpEmailView.text.toString()
            val password : String = binding.sginUpPasswordView.text.toString()

            CheckClass.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()){ task ->
                    binding.sginUpEmailView.text.clear()
                    binding.sginUpPasswordView.text.clear()

                    if(task.isSuccessful) {
                        // 비밀번호 6자리 이상
                        // 메일 보내기
                        CheckClass.auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { sendTask ->
                            if(sendTask.isSuccessful){
                                Toast.makeText(requireContext(), "회원가입 성공 전송된 메일을 확인해주세요.", Toast.LENGTH_SHORT).show()

                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.fragmentSign, LoginFragment())
                                    .commit()
                            }
                            else {
                                Toast.makeText(requireContext(), "메일 전송 실패.", Toast.LENGTH_SHORT).show()

                            }
                        }
                    }
                    else {
                        Toast.makeText(requireContext(), "회원 가입 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.cancelBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentSign, LoginFragment())
                .commit()
        }

        return binding.root
    }


}