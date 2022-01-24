package com.example.todolist

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.databinding.FragmentTodoMainBinding
import com.example.todolist.databinding.ItemHeaderBinding
import com.example.todolist.databinding.ItemMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Console


// 항목 뷰를 가지는 역할
class ViewHolder(val binding: ItemMainBinding) : RecyclerView.ViewHolder(binding.root)

class HeaderViewHolder(val binding: ItemHeaderBinding) : RecyclerView.ViewHolder(binding.root)


// 항목 구성자 어댑터
class Adapter(private val longClickAction: () -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_HEADER = 0
    private val TYPE_ITEM  = 1
    private var deleteMode = false
        set(value) {
            field = value
            datas.forEach {
                if (it is TodoUIModel.Data) {
                    if (!field) {
                        it.isSelected = false
                    }
                }
            }
            notifyDataSetChanged()
        }

    // 삭제모드 boolean

    var datas: MutableList<TodoUIModel> = mutableListOf()

    init {
        Log.d("Test","${CheckClass.email}")
        CheckClass.db?.collection("${CheckClass.email}")?.addSnapshotListener { s, fe ->
            // 데이터베이스에있는 문서를 하나하나 가져옴
            s?.documents?.forEach { doc->
                // 데이터베이스 문서의 이름
                val title = doc.id
                // 데이터베이스의 문서를 참조값으로 바꾼뒤 문서 내에있는 콜렉션 호출
                doc.reference.collection("List").addSnapshotListener { subSnap, fe ->
                    var header: TodoUIModel.Header? = null
                    if (subSnap?.documents?.isNotEmpty() == true) {
                        header = TodoUIModel.Header(title)
                        datas.add(header)
                    }


                    subSnap?.documents?.mapNotNull { sub ->
                        header?.subItem?.add(sub.id)
                        sub?.toObject(FirestoreTodoData::class.java)
                            ?.toUIModel {
                                header?.subItem?.remove(sub.id)
                                doc.reference.collection("List").document(sub.id).delete()
                                    .addOnCompleteListener { notifyDataSetChanged() }
                            }
                    }?.sortedBy { it.time }?.forEach(datas::add)
//                    subSnap?.documents?.forEach { sub ->
//                        // add todo item
//
//                        val item = sub?.toObject(FirestoreTodoData::class.java)
//                            ?.toUIModel {
//                                sub.id.let { subId->
//                                    doc.reference.collection("List").document(subId).delete()
//                                        .addOnCompleteListener { notifyDataSetChanged() }
//                                }
//                            }
//
//                        if (item != null) {
//                            datas.add(item)
//                        }
//                    }
                    notifyDataSetChanged()
                }
            }
        }
    }

    // 항목 갯수를 판단하기 위해 호출
    override fun getItemCount(): Int {
        return datas.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (datas[position]) {
            is TodoUIModel.Header -> TYPE_HEADER
            is TodoUIModel.Data -> TYPE_ITEM
            else -> TODO("not supported")
        }
    }
    // 항목 뷰를 가지는 뷰 홀더를 준비하기 위해 호출
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            TYPE_HEADER ->
                HeaderViewHolder(ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else ->
                ViewHolder(ItemMainBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    // 각 항목을 구성하기 위해 호출
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val item = datas[position]

        // 뷰에 데이터 출력
        if(holder is ViewHolder && item is TodoUIModel.Data) {
            // ui 상태 값 초기화
            val binding = holder.binding
            binding.todoTime.text = item.time
            binding.todoMain.text = item.todomain
            binding.todoSub.text = item.todosub
            binding.itemRoot.setBackgroundColor(item.getBgColor())
            binding.doneCheck.isChecked = item.isDone

            // ux 바인딩
            binding.doneCheck.setOnCheckedChangeListener { _, isChecked ->
                item.isDone = isChecked
                binding.itemRoot.setBackgroundColor(item.getBgColor())
            }

            binding.itemRoot.setOnClickListener {
                if (deleteMode) {
                    item.isSelected = !item.isSelected
                    binding.itemRoot.setBackgroundColor(item.getBgColor())
                }
            }
            binding.itemRoot.setOnLongClickListener {
                deleteMode = !deleteMode
                if (deleteMode) {
                    item.isSelected = true
                }
                binding.itemRoot.setBackgroundColor(item.getBgColor())
                longClickAction()
                true
            }
        }else if (holder is HeaderViewHolder && item is TodoUIModel.Header){
            val headerBinding = holder.binding
            headerBinding.headerTitle.text = item.date
        }

//        binding.deleteCheck.isVisible = isCheckboxVisible
    }

}

class TodoMainFragment : Fragment() {
    lateinit var binding : FragmentTodoMainBinding
    private var isFabOpen = false

    private val adapter: Adapter by lazy {
        Adapter {
            val menuItem = requireActivity()
                .findViewById<Toolbar>(R.id.toolbar)
                ?.menu?.findItem(R.id.deleteDone) ?: return@Adapter
            menuItem.isVisible = !menuItem.isVisible
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentTodoMainBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)


        binding.floating.setOnClickListener {
            fabToggle()
        }

        binding.fabAdd.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentCon, TodoAddFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.fabDelete.setOnClickListener{
            val menuItem = requireActivity()
                .findViewById<Toolbar>(R.id.toolbar)
                ?.menu?.findItem(R.id.deleteDone) ?: return@setOnClickListener
            menuItem.isVisible = !menuItem.isVisible
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)

        return binding.root
    }

    private fun fabToggle() {

        // 플로팅 액션 버튼 닫기 - 열려있는 플로팅 버튼 집어넣는 애니메이션 세팅
        if (isFabOpen) {
            ObjectAnimator.ofFloat(binding.fabAdd, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabDelete, "translationY", 0f).apply { start() }

            // 플로팅 액션 버튼 열기 - 닫혀있는 플로팅 버튼 꺼내는 애니메이션 세팅
        } else {
            ObjectAnimator.ofFloat(binding.fabAdd, "translationY", -200f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabDelete, "translationY", -400f).apply { start() }
        }

        isFabOpen = !isFabOpen
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_toolbar, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.deleteDone -> {
                val datass = adapter.datas.filter {
                    if (it is TodoUIModel.Data && it.isSelected) {
                        it.deleteAction()
                        false
                    } else {
                        true
                    }
                }
                adapter.datas = datass.filter {
                    if (it is TodoUIModel.Header) {
                        it.subItem.isNotEmpty()
                    } else {
                        true
                    }
                }.toMutableList()
                adapter.notifyDataSetChanged()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}