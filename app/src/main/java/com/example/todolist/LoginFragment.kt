package com.example.todolist

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.example.todolist.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {
    lateinit var binding : FragmentLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container,false)

        binding.loginBtn.setOnClickListener {
            // 이메일, 비밀번호 로그인
            val email : String = binding.emailEditView.text.toString()
            val password : String = binding.passwordEditView.text.toString()

            CheckClass.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    binding.emailEditView.text.clear()
                    binding.passwordEditView.text.clear()

                    if (task.isSuccessful) {
                        if (CheckClass.checkAuth()) {
                            // 로그인 성공
                            CheckClass.email = email

                            val nextIntent = Intent(requireActivity(), MainActivity::class.java)
                            startActivity(nextIntent)
                        }
                        else{
                            // 발송된 메일로 인증을 하지않았을경우
                            Toast.makeText(requireContext(), "이메일 인증이 되지 않았습니다.", Toast.LENGTH_SHORT).show()

                        }
                    }
                    else{
                        Toast.makeText(requireContext(), "로그인 실패.", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.signUpBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentSign, SginUpFragment())
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }


}