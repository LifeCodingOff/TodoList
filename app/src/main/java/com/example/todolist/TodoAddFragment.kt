package com.example.todolist

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.todolist.databinding.FragmentTodoAddBinding
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.SetOptions
import java.text.SimpleDateFormat
import java.util.*

class TodoAddFragment : Fragment() {

    lateinit var binding : FragmentTodoAddBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTodoAddBinding.inflate(inflater, container, false)

        binding.dateBtn.setOnClickListener {
            val cal = Calendar.getInstance()
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

                cal.set( year, month, dayOfMonth)

                val formatCal = formatter.format(cal.time)

                binding.dateText.text = formatCal

                cal.clear()
            }

            DatePickerDialog(requireContext(), dateSetListener, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()

        }


        binding.timeBtn.setOnClickListener {
            val cal = Calendar.getInstance()
            val formatter = SimpleDateFormat("HH:mm")
            val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->

                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)

                val formatTime = formatter.format(cal.time)

                binding.timeText.text = formatTime

                cal.clear()
            }
            TimePickerDialog(requireContext(), timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),true).show()

        }

        binding.addBtn.setOnClickListener {
            val addTodoData = mapOf(
                "time" to binding.timeText.text.toString(),
                "todomain" to binding.todoMainEdit.text.toString(),
                "todosub" to binding.todoSubEdit.text.toString()
            )

            val createDoc = mapOf("uid" to CheckClass.email.toString())

            val checkDoc = CheckClass.db?.collection("${CheckClass.email}")

            checkDoc?.document("${binding.dateText.text}")?.get()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val doc = task.result

                    if (doc != null) {
                        if (!doc.exists()) {
                            checkDoc.document("${binding.dateText.text}").set(createDoc)
                        }
                        checkDoc.document("${binding.dateText.text}").collection("List").document().set(addTodoData)

                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragmentCon, TodoMainFragment())
                            .commit()

                    } else {
                        Log.d("TAG", "Error: ", task.exception)
                    }
                }
            }


        }


        return binding.root
    }


}
