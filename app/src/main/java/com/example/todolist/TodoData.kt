package com.example.todolist

// sealed class는 자신을 상속받는 서브클래스를 만들수있음
// enum처럼 열거형처럼 쓸수있다
// Sealed class는 Super class를 상속받는 Child 클래스의 종류 제한하는 특성을 갖고 있는 클래스입니다.
// 어뎁터에서 뷰홀더나를 쉽게 변경하기 위해 when과 같이 사용
sealed class TodoUIModel {
    data class Header(var date : String? = null, val subItem: MutableSet<String> = mutableSetOf()) : TodoUIModel()
    data class Data(
        var isSelected: Boolean,
        var isDone: Boolean,
        val deleteAction: () -> Unit,
        var time : String?, var todomain: String?, var todosub: String?) : TodoUIModel() {
        fun getBgColor(): Int {
            return (when {
                isSelected -> {
                    0xFF888888
                }
                isDone -> {
                    0xFF00FF00
                }
                else -> {
                    0xFFFFFFFF
                }
            }).toInt()
        }
    }
}

class FirestoreTodoData(var time : String? = null, var todomain: String? = null, var todosub: String? = null)

fun FirestoreTodoData.toUIModel(deleteAction: () -> Unit) = TodoUIModel.Data(
    isSelected = false,
    isDone = false,
    deleteAction = deleteAction,
    time = this.time,
    todomain = this.todomain,
    todosub = this.todosub,
)

