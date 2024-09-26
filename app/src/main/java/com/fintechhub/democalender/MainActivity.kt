package com.fintechhub.democalender

import android.annotation.SuppressLint
import android.app.Fragment
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.fragment.app.FragmentActivity
import com.fintechhub.democalender.ui.theme.DemoCalenderTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val sampleEvents = remember {
                mutableStateListOf(
                    Event(1, "Event 1", Calendar.getInstance().apply { set(2024, 8, 20) }.time),
                    Event(2, "Event 2", Calendar.getInstance().apply { set(2024, 8, 25) }.time),
                    Event(3, "Event 3", Calendar.getInstance().apply { set(2024, 8, 26) }.time),
                    Event(4, "Event 4", Calendar.getInstance().apply { set(2024, 8, 26) }.time),
                    Event(5, "Event 5", Calendar.getInstance().apply { set(2024, 9, 26) }.time)
                )
            }
            DemoCalenderTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp)
                ) {
                    CalendarWithEvents(events = sampleEvents)
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CalendarWithEvents(events: MutableList<Event>) {
    // MutableState for current month
    val currentMonth = remember { mutableStateOf(Calendar.getInstance()) }
    // MutableState for selected date events
    val selectedDateEvents = remember { mutableStateOf<List<Event>>(listOf()) }
    var showDialog by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var setData by remember { mutableStateOf("") }
    val state = rememberTimePickerState()
    // Pager state to handle page changes
    val pagerState = rememberPagerState(initialPage = Int.MAX_VALUE / 2)

    // UI Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Button to create an event
        Button(onClick = { showDialog = true }) {
            Text("Create Event")
        }
        Button(onClick = { showTimePicker = true }) {
            Text("Time Event")
        }
        setData.let { state ->
            Text(
                text = "Selected Time: $setData",
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        if (showTimePicker) {
            TimePickerDialog(
                onCancel = { showTimePicker = false },
                onConfirm = {
                    val cal = Calendar.getInstance()
                    cal.set(Calendar.HOUR_OF_DAY, state.hour)
                    cal.set(Calendar.MINUTE, state.minute)
                    cal.isLenient = false
                    setData = String.format("%02d:%02d", state.hour, state.minute)
                    showTimePicker = false
                },
            ) {
                TimePicker(state = state)
            }
        }
        Spacer(modifier = Modifier.height(60.dp))

        if (showDialog) {
            CreateEventDialog(
                onCreate = { newEvent ->
                    events.add(newEvent)
                    showDialog = false
                },
                onDismiss = { showDialog = false }
            )
        }

        HorizontalPager(
            count = Int.MAX_VALUE,
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            verticalAlignment = Alignment.Top,
        ) { page ->
            val month = currentMonth.value.clone() as Calendar
            month.add(Calendar.MONTH, page - Int.MAX_VALUE / 2)
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                CalendarImage(month)
                // Calendar controls
                CalendarControls(pagerState, month, selectedDateEvents)

                // Display the names of the days of the week
                DayNames()

                // Calendar grid with events
                CalendarGrid(events, month, selectedDateEvents)

                // Display events for selected date at the bottom
                EventList(selectedDateEvents.value)
            }
        }
    }
}

@Composable
fun DayNames() {
    val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        daysOfWeek.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun CalendarImage(calendar: Calendar) {
    val month = calendar.get(Calendar.MONTH)
    val monthImage = when (month) {
        1 -> R.drawable.one
        2 -> R.drawable.two
        3 -> R.drawable.one
        4 -> R.drawable.two
        5 -> R.drawable.one
        6 -> R.drawable.two
        7 -> R.drawable.one
        8 -> R.drawable.two
        9 -> R.drawable.one
        10 -> R.drawable.two
        11 -> R.drawable.one
        0 -> R.drawable.two
        else -> R.drawable.ic_launcher_background
    }
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = monthImage),
            contentDescription = "Month Image",
            contentScale = ContentScale.Crop
        )
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun CalendarControls(
    pagerState: PagerState,
    month: Calendar,
    selectedDateEvents: MutableState<List<Event>>
) {
    val coroutineScope = rememberCoroutineScope()

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        IconButton(onClick = {
            // Move to the previous month
            selectedDateEvents.value = emptyList()
            coroutineScope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage - 1)
            }
        }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Previous Month")
        }

        // Display current month and year dynamically
        Text(
            text = "${
                month.getDisplayName(
                    Calendar.MONTH,
                    Calendar.LONG,
                    Locale.getDefault()
                )
            } ${month.get(Calendar.YEAR)}",
            style = MaterialTheme.typography.bodySmall,
        )

        IconButton(onClick = {
            // Move to the next month
            selectedDateEvents.value = emptyList()
            coroutineScope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
        }) {
            Icon(Icons.Default.ArrowForward, contentDescription = "Next Month")
        }
    }
}

@Composable
fun CalendarGrid(
    events: List<Event>,
    currentMonth: Calendar,
    selectedDateEvents: MutableState<List<Event>>
) {
    val daysInMonth = currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfMonth = currentMonth.clone() as Calendar
    firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1)
    val startingDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1
    val days = (1..daysInMonth).toList()
    val paddingDaysBefore = List(startingDayOfWeek) { -1 }
    val paddingDaysAfter = List((7 - (startingDayOfWeek + daysInMonth) % 7) % 7) { -1 }
    val allDays = paddingDaysBefore + days + paddingDaysAfter

    val today = Calendar.getInstance()
    LazyVerticalGrid(columns = GridCells.Fixed(7)) {
        items(allDays.size) { index ->
            val day = allDays[index]
            val textColor = if (day <= 0) Color.Transparent else Color.Black
            val currentDay = if (day <= 0) "" else day.toString()
            val eventsForDay = events.filter { event ->
                val eventCalendar = Calendar.getInstance()
                eventCalendar.time = event.date
                eventCalendar.get(Calendar.DAY_OF_MONTH) == day && eventCalendar.get(Calendar.MONTH) == currentMonth.get(
                    Calendar.MONTH
                )
            }
            // Display dots for each event
            val eventDots = buildString {
                repeat(eventsForDay.size) {
                    append("• ")
                }
            }
            // Check if the day is today
            val isToday =
                day == today.get(Calendar.DAY_OF_MONTH) && currentMonth.get(Calendar.MONTH) == today.get(
                    Calendar.MONTH
                ) && currentMonth.get(Calendar.YEAR) == today.get(Calendar.YEAR)

            // Use Box to center content and set size for circular shape
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        selectedDateEvents.value = eventsForDay
                    }
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isToday) Color.Blue else Color.Transparent),
                contentAlignment = Alignment.Center // Center content within Box
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = currentDay,
                        color = if (isToday) Color.White else textColor,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Text(
                        text = eventDots,
                        color = Color.Red,
                        modifier = Modifier.offset(y = -8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EventList(events: List<Event>) {
    Column(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        events.forEach { event ->
            Text(text = event.title)
        }
    }
}

// Preview Function
@Preview(showBackground = true)
@Composable
fun PreviewCalendar() {
    DemoCalenderTheme {
        CalendarWithEvents(events = mutableListOf())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventDialog(onCreate: (Event) -> Unit, onDismiss: () -> Unit) {
    var eventTitle by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(Date()) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    // Function to show DatePickerDialog
    fun showDatePicker() {
        val datePickerDialog = android.app.DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                // Update the selected date when a date is picked
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                selectedDate = calendar.time
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Event") },
        text = {
            Column {
                TextField(
                    value = eventTitle,
                    onValueChange = { eventTitle = it },
                    label = { Text("Event Title") }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Date display and picker button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Selected Date: ${dateFormat.format(selectedDate)}")
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { showDatePicker() }) {
                        Text("Select Date")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newEvent = Event(System.currentTimeMillis(), eventTitle, selectedDate)
                    onCreate(newEvent)
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

}

@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            toggle()
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = onCancel
                    ) { Text("Cancel") }
                    TextButton(
                        onClick = onConfirm
                    ) { Text("OK") }
                }
            }
        }
    }
}