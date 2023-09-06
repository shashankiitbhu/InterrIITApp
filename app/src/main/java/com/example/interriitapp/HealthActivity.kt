package com.example.interriitapp


import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.interriitapp.Utility.Task
import com.example.interriitapp.Utility.TaskManager
import com.example.interriitapp.ui.theme.InterrIITAppTheme
import java.util.*


class HealthActivity : ComponentActivity() {
    private val CHANNEL_ID = "reminder_channel"
    @SuppressLint("ScheduleExactAlarm")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()
        val taskManager = TaskManager(applicationContext)


        setContent {
            InterrIITAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var selectedOption by remember { mutableStateOf("") }
                    val showDialog = remember{ mutableStateOf(false)}
                    val taskMan = remember{ mutableStateOf(taskManager)}

                    Scaffold(
                        floatingActionButton = {
                            ExtendedFloatingActionButton(
                                onClick = {
                                    showDialog.value = true
                                },
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.primary,
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                                Text(text = "Add Reminder")
                            }
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(it)
                        ) {
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                shape = MaterialTheme.shapes.medium,
                                elevation = CardDefaults.cardElevation(4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "Create a Health Reminder",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "You can create a reminder for drinking water or taking medication. You can also set the time at which you want to be reminded. You will receive a notification at the set time. You can also add extra message with your reminders.",
                                        fontSize = 16.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            if(taskMan.value.getAllTasks().isNotEmpty()){
                                Text(
                                    text = "Your Reminders",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(8.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                TaskList(tasks = taskMan.value.getAllTasks())
                            }
                        }

                        if(showDialog.value){
                            ReminderDialog(taskMan,showDialog)
                        }
                    }
                }
            }
        }
    }


    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val name = "Reminder Channel"
            val descriptionText = "Reminder notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }



    @SuppressLint("ScheduleExactAlarm")
    @OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
    @Composable
    fun ReminderDialog(
        taskManager : MutableState<TaskManager>,
        showDialog: MutableState<Boolean>
    ) {
        var selectedOption by remember { mutableStateOf("") }
        var selectedTime by remember { mutableStateOf(Calendar.getInstance()) }
        val context = LocalContext.current
        var reminderText by remember { mutableStateOf(TextFieldValue()) }

        AlertDialog(
            onDismissRequest = { },
            title = { Text(text = "Select Reminder") },
            confirmButton = {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        TextField(
                            value = reminderText,
                            onValueChange = {
                                reminderText = it
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Reminder Message") },
                            shape = RoundedCornerShape(8.dp),
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = Color.Black,
                                containerColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            singleLine = true,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = {
                                selectedOption = "Water"
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (selectedOption == "Water") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                                contentColor = if (selectedOption == "Water") Color.White else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(text = "Water")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = {
                                selectedOption = "Medication"
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                                ,
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (selectedOption == "Medication") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                                contentColor = if (selectedOption == "Medication") Color.White else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(text = "Medication")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = {
                            // Show Time Picker Dialog
                            val timePicker = TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                    selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                    selectedTime.set(Calendar.MINUTE, minute)
                                },
                                selectedTime.get(Calendar.HOUR_OF_DAY),
                                selectedTime.get(Calendar.MINUTE),
                                false
                            )
                            timePicker.show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Select Time")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (selectedOption.isNotEmpty()) {

                                taskManager.value.addTask(
                                    Task(
                                        title = "${selectedOption}: ${reminderText.text}",
                                        time = "${selectedTime.get(Calendar.HOUR_OF_DAY)}:${selectedTime.get(Calendar.MINUTE)}"
                                    )
                                )

                                val calendar = Calendar.getInstance()
                                calendar.set(Calendar.HOUR_OF_DAY, selectedTime.get(Calendar.HOUR_OF_DAY))
                                calendar.set(Calendar.MINUTE, selectedTime.get(Calendar.MINUTE))
                                calendar.set(Calendar.SECOND, 0)
                                calendar.set(Calendar.MILLISECOND, 0)
                                val timeInMillis = calendar.timeInMillis
                                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                val intent = Intent(context, AlarmReceiver::class.java).apply {
                                    putExtra("EXTRA_MESSAGE", "${selectedOption}: ${reminderText.text}")
                                }

                                alarmManager.setExactAndAllowWhileIdle(
                                    AlarmManager.RTC_WAKEUP,
                                    timeInMillis,
                                    PendingIntent.getBroadcast(
                                        context,
                                        0,
                                        intent,
                                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                                    )
                                )


                                showDialog.value = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = (selectedOption.isNotEmpty() && selectedTime != null)
                    ) {
                        Text(text = "Create Reminder")
                    }
                }
            }
        )
    }

}
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val taskManager = TaskManager(context!!)
        Log.d("AlarmScheduler", "Alarm set called")
        val message = intent?.getStringExtra("EXTRA_MESSAGE")
        taskManager.removeTask(Task(title = message?:"", time = ""))
        if (context != null) {
            Log.d("AlarmScheduler", "Alarm set ${message} ")
            sendNotification(context, message!!)

        }

    }

    private fun sendNotification(context: Context, message: String) {
        val intent = Intent(context, HealthActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, "reminder_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Reminder to ${if(message.contains("Water")) "Drink Water" else "Take Medication"}")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(
                        context as HealthActivity,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        1)
                }

                return
            }
            notify(0, builder.build())
        }
    }
}

@Composable
fun TaskItem(task: Task) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            Text(
                text = task.time,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}
@Composable
fun TaskList(tasks: List<Task>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(tasks) { task ->
            TaskItem(task = task)
        }
    }
}

