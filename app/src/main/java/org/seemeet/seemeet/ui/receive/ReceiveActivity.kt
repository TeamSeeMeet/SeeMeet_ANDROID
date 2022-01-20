package org.seemeet.seemeet.ui.receive

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Parcelable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import org.seemeet.seemeet.R
import org.seemeet.seemeet.databinding.ActivityReceiveBinding
import org.seemeet.seemeet.ui.receive.adapter.ReceiveCheckListAdapter
import org.seemeet.seemeet.ui.receive.adapter.ReceiveSchduleListAdapter
import org.seemeet.seemeet.ui.viewmodel.ReceiveViewModel


class ReceiveActivity : AppCompatActivity() {

    private lateinit var binding:  ActivityReceiveBinding
    private val viewModel: ReceiveViewModel by viewModels() //위임초기화
    private var invitationId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_receive)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        invitationId =  intent.getIntExtra("invitationId", -1)
        Log.d("********RECEIVE_INVITATION_ID", invitationId.toString())

        if(invitationId != -1){
            viewModel.requestReceiveInvitation(invitationId)
        }

        setSingleChoice()

        setCheckBoxAdapter()
        setClickedAdapter()

        setListObserver()
        setTextColorSpan()

        initButtonClick()

    }

    private fun setTextColorSpan(){
        val str = resources.getString(R.string.recieve_choice_msg)
        val word = "날짜를 선택"
        val start = str.indexOf(word)
        val end = start + word.length
        val ss = SpannableStringBuilder(str)

        ss.setSpan(ForegroundColorSpan(Color.parseColor("#FA555C")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(RelativeSizeSpan(1.2f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvRecieveCheckboxMsg.text = ss
    }

    //리싸이클러 뷰 단일 선택 용 함수
    private fun setSingleChoice(){

        //체크박스 있는 리사이클러뷰 클릭 시  _ 아래에 있는 일정 리마인더에 데이터가 셋팅됨.
        binding.rvReceiveCheckbox.addOnItemTouchListener(object :
            RecyclerView.OnItemTouchListener{
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if(e.action != MotionEvent.ACTION_MOVE){
                    val child = rv.findChildViewUnder(e.x, e.y)
                    if(child != null){
                        val position = rv.getChildAdapterPosition(child)
                        val view = rv.layoutManager?.findViewByPosition(position)
                        view?.setBackgroundResource(R.drawable.rectangle_with_blackline)
                        Log.d("*******************tag", viewModel.receiveInvitationDateList.value!![position].end)

                        // 지금은 일괄적으로 다 같은 데이터 넣고 있는데, 나중에 위의 체크박스에서 포지션 가지고 id 값 불러와서 서버에 해당 데이터 요청하기...
                        viewModel.requestReceivePlanResponse(viewModel.receiveInvitationDateList.value!![position].id)
                        binding.tvReceiveSMsg.visibility = View.GONE

                        for(i in 0..rv.adapter!!.itemCount){
                            val otherView = rv.layoutManager?.findViewByPosition(i)
                            if(otherView != view){
                                otherView?.setBackgroundResource(R.color.white)
                            }
                        }
                    }
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

            }
        })
    }

    // 어댑터
    private fun setCheckBoxAdapter() {
        val checkboxListAdapter = ReceiveCheckListAdapter( onClickCheckbox = { viewModel.setIsClicked(it)} )

        binding.rvReceiveCheckbox.adapter = checkboxListAdapter
    }

    private fun setClickedAdapter() {
        val clickedAdapter = ReceiveSchduleListAdapter()

        binding.rvReceiveSchdule.adapter = clickedAdapter
    }

    // 옵저버
    private fun setListObserver() {

        viewModel.receiveInvitationData.observe(this, Observer {
            receiveInvitationData ->

            viewModel.setReceiveInvitationDate()

            receiveInvitationData.newGuests.forEach{
                binding.cgRecieve.addView(Chip(this).apply{
                    text = it.username

                    setChipBackgroundColorResource(R.color.white)
                    setTextAppearance(R.style.chipTextPinkStyle)
                    chipStrokeWidth = 1.0F
                    setChipStrokeColorResource(R.color.pink01)
                    isCheckable = false
                    isEnabled = false

                })
                Log.d("**********************받은이", it.username)
            }

        })

        viewModel.receiveInvitationDateList.observe(this, Observer {
            checkboxList ->
            with(binding.rvReceiveCheckbox.adapter as ReceiveCheckListAdapter){
                setCheckBox(checkboxList)
            }
        })


        viewModel.receivePlanResponseList.observe(this, Observer {
            clickedList -> with(binding.rvReceiveSchdule.adapter as ReceiveSchduleListAdapter){
                setSchedule(clickedList)
                if(clickedList.isEmpty()) {
                    Log.d("************PLAN_RESPONSE", "EMPTY")
                    binding.tvReceiveSMsg.text = "약속이 없어요."
                    binding.tvReceiveSMsg.visibility = View.VISIBLE
                    binding.tvReceiveClickDay.visibility = View.GONE
                }else {
                    Log.d("************PLAN_RESPONSE", "NOT EMPTY")
                    binding.tvReceiveClickDay.text = clickedList[0].date
                    binding.tvReceiveSMsg.visibility = View.GONE
                }
             }
        })

        viewModel.isClicked.observe(this, Observer {
            it ->
            Log.d("***************isClicked", it.toString())
            if(it > 0) {
                binding.btnReceiveYes.isClickable = true
                binding.btnReceiveYes.isEnabled = true
                binding.btnReceiveYes.background = resources.getDrawable(R.drawable.rectangle_pink01_10)
            }
            else {
                binding.btnReceiveYes.isClickable = false
                binding.btnReceiveYes.isEnabled = false
                binding.btnReceiveYes.background = resources.getDrawable(R.drawable.rectangle_gray02_10)
            }
        })

    }

    private fun initButtonClick(){

        //맨 아래 취소 버튼
        binding.btnReceiveNo.setOnClickListener {
            var dialogView = ReceiveNoDialogFragment()
            val bundle = Bundle()

            //서버 달 때 고치자. cancel 시에는 초대장 id가 있으면 될듯.


            dialogView.arguments = bundle

            dialogView.setButtonClickListener( object :  ReceiveNoDialogFragment.OnButtonClickListener {
                override fun onCancelNoClicked() {

                }

                override fun onCancelYesClicked() {
                    //여기서 데이터 전송.
                    //위의 cblist에서 flag가 true인 애들 아이디만 골라서 전송해주기.
                }
            })
            dialogView.show(supportFragmentManager, "send wish checkbox time")
        }

        //맨 아래 수락 버튼
        binding.btnReceiveYes.setOnClickListener {
            var dialogView = ReceiveYesDiagloFragment()
            val bundle = Bundle()
            val cblist = (binding.rvReceiveCheckbox.adapter as ReceiveCheckListAdapter).getCheckBoxList()

            bundle.putParcelableArrayList("cblist", cblist as ArrayList<out Parcelable>)
            dialogView.arguments = bundle


            dialogView.setButtonClickListener( object : ReceiveYesDiagloFragment.OnButtonClickListener {
                override fun onSendClicked() {
                    //여기서 데이터 전송.
                    //위의 cblist에서 flag가 true인 애들 아이디만 골라서 전송해주기.
                }

                override fun onCancelClicked() {

                }
            })

            dialogView.show(supportFragmentManager, "send wish checkbox time")
        }

        binding.ivReceiveBack.setOnClickListener {
            finish()
        }
    }


    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ReceiveActivity::class.java)
            context.startActivity(intent)
        }
    }
}