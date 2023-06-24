package com.android.sharedshoppinglist.app

import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged

import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.android.sharedshoppinglist.R
import com.android.sharedshoppinglist.alarm.AlarmReceiver
import com.android.sharedshoppinglist.database.DatabaseHelper
import com.android.sharedshoppinglist.databinding.FragmentNewListBinding
import java.util.*


class NewListFragment : Fragment() {


    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private var switchState : String = "Off"
    private var dat : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val binding: FragmentNewListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_new_list, container,false)

        val tempCalendar: Calendar = Calendar.getInstance()

        binding.timeEditText.isEnabled = false
        binding.dateEditText.isEnabled = false
        binding.addNewListBtn.isEnabled = false

        binding.dateEditText.setOnClickListener {
            val c = Calendar.getInstance()

            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = container?.let { it1 ->
                DatePickerDialog(

                    it1.context,
                    { _, year, monthOfYear, dayOfMonth ->

                        dat = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                        tempCalendar.set(Calendar.YEAR, year)
                        tempCalendar.set(Calendar.MONTH, monthOfYear)
                        tempCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        binding.dateEditText.setText(dat)
                    },
                    year,
                    month,
                    day
                )
            }
            datePickerDialog?.datePicker?.minDate = System.currentTimeMillis()
            datePickerDialog?.show()
        }

        binding.timeEditText.setOnClickListener {
            val c = Calendar.getInstance()

            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                activity,
                { _, hourOfDay, minuteOfDay ->

                    tempCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    tempCalendar.set(Calendar.MINUTE, minuteOfDay)

                    if(minuteOfDay < 10){
                        val time = "$hourOfDay:0$minuteOfDay"
                        binding.timeEditText.setText(time)
                    }else{
                        val time = "$hourOfDay:$minuteOfDay"
                        binding.timeEditText.setText(time)
                    }

                },
                hour,
                minute,
                false
            )
            timePickerDialog.show()
        }

        binding.reminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switchState = "On"
                binding.timeEditText.isEnabled = true
                binding.dateEditText.isEnabled = true
            } else {
                switchState = "Off"
                binding.timeEditText.isEnabled = false
                binding.dateEditText.isEnabled = false
                binding.dateEditText.text.clear()
                binding.timeEditText.text.clear()
            }
        }

        binding.listNameEditText.doOnTextChanged{_, _, _, _ ->
            binding.addNewListBtn.isEnabled = binding.listNameEditText.text.toString().isNotEmpty()
        }

        binding.addNewListBtn.setOnClickListener {

            val db = activity?.let { it1 -> DatabaseHelper(it1) }

            val name = binding.listNameEditText.text.toString()
            val date = binding.dateEditText.text.toString()
            val time = binding.timeEditText.text.toString()
            val reminder = switchState

            val reqReqCode = (0..1000000000).random()

            val year = tempCalendar.get(Calendar.YEAR)
            val month = tempCalendar.get(Calendar.MONTH)

            val day = tempCalendar.get(Calendar.DAY_OF_MONTH)
            val hour = tempCalendar.get(Calendar.HOUR_OF_DAY)
            val minute = tempCalendar.get(Calendar.MINUTE)

            if(switchState == "On" && binding.dateEditText.text.toString().isNotEmpty() && binding.timeEditText.text.toString().isNotEmpty()) {
                setAlarm(day, month, year, hour, minute, reqReqCode)

                db?.insertList(name, date, time, reminder, reqReqCode)

                binding.listNameEditText.text.clear()
                binding.dateEditText.text.clear()
                binding.timeEditText.text.clear()
                binding.reminderSwitch.isChecked = false

                view?.findNavController()?.navigate(R.id.action_newListFragment_to_listsFragment)

                Toast.makeText(this.context, "Alarm set for this list", Toast.LENGTH_SHORT).show()
            }else if (switchState == "On" && !(binding.dateEditText.text.toString().isNotEmpty() && binding.timeEditText.text.toString().isNotEmpty())){
                Toast.makeText(this.context, "Please select date and time", Toast.LENGTH_SHORT).show()
            } else if (switchState == "Off"){

                db?.insertList(name, date, time, reminder, reqReqCode)

                binding.listNameEditText.text.clear()
                binding.dateEditText.text.clear()
                binding.timeEditText.text.clear()
                binding.reminderSwitch.isChecked = false

                view?.findNavController()?.navigate(R.id.action_newListFragment_to_listsFragment)

                Toast.makeText(this.context, "Alarm not set for this list", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

    private fun createNotificationChannel() {

        val name = "Alarm clock Channel"
        val description = "Reminder Alarm manager"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val notificationChannel = NotificationChannel("111", name, importance)
        notificationChannel.description = description
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun setAlarm(alarmDay : Int, alarmMonth: Int, alarmYear: Int,
                            alarmHour : Int, alarmMinute: Int, requestCode: Int) {

        alarmManager = context?.applicationContext?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        pendingIntent = Intent(context?.applicationContext, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context?.applicationContext, requestCode, intent, FLAG_IMMUTABLE)
        }

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, alarmHour)
        calendar.set(Calendar.MINUTE, alarmMinute)
        calendar.set(Calendar.DAY_OF_MONTH, alarmDay)
        calendar.set(Calendar.MONTH, alarmMonth)
        calendar.set(Calendar.YEAR, alarmYear)

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

}