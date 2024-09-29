package com.fintechhub.democalender

import android.Manifest
import android.annotation.SuppressLint
import android.app.Fragment
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val sampleEvents = remember {
                mutableStateListOf(
                    Event(
                        1,
                        "Event 1",
                        "this is ",
                        Calendar.getInstance().apply { set(2024, 8, 20) }.time,
                        false,
                        "10:20",
                        "22:23"
                    ),
                    Event(
                        2,
                        "Event 2",
                        "this is ",
                        Calendar.getInstance().apply { set(2024, 10, 20) }.time,
                        false,
                        "10:20",
                        "22:23"
                    ),
                    Event(
                        3,
                        "Event 3",
                        "this is ",
                        Calendar.getInstance().apply { set(2024, 8, 20) }.time,
                        false,
                        "10:20",
                        "22:23"
                    ),
                    Event(
                        4,
                        "Event 4",
                        "this is ",
                        Calendar.getInstance().apply { set(2024, 10, 25) }.time,
                        false,
                        "10:20",
                        "22:23"
                    ),
                    Event(
                        5,
                        "Event 5",
                        "this is ",
                        Calendar.getInstance().apply { set(2024, 8, 30) }.time,
                        false,
                        "10:20",
                        "22:23"
                    ),
                )
            }
            var showDialog by remember { mutableStateOf(false) }
            if (showDialog) {
                CreateEventDialog(onCreate = { newEvent ->
                    sampleEvents.add(newEvent)
                    showDialog = false
                }, onDismiss = { showDialog = false })
            }
            DemoCalenderTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                    TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        actionIconContentColor = Color.Black,
                        navigationIconContentColor = Color.Black,
                        scrolledContainerColor = Color.White,
                        titleContentColor = Color.Black
                    ), title = {
                        Text(
                            "Celender",
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }, actions = {
                        IconButton(onClick = { showDialog = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_addchart_24),
                                contentDescription = "Back",
                                tint = Color(0xff2c87d9)
                            )
                        }
                    }, navigationIcon = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    })
                }) {
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
    // Pager state to handle page changes
    val pagerState = rememberPagerState(initialPage = Int.MAX_VALUE / 2)

    // UI Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 60.dp)
    ) {
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
    pagerState: PagerState, month: Calendar, selectedDateEvents: MutableState<List<Event>>
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
                    Calendar.MONTH, Calendar.LONG, Locale.getDefault()
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
    events: List<Event>, currentMonth: Calendar, selectedDateEvents: MutableState<List<Event>>
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
            Box(modifier = Modifier
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
                        text = eventDots, color = Color.Red, modifier = Modifier.offset(y = -8.dp)
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

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventDialog(onCreate: (Event) -> Unit, onDismiss: () -> Unit) {
    var eventTitle by remember { mutableStateOf("") }
    var eventDescriptor by remember { mutableStateOf("") }
    var selectedCalenderData by remember { mutableStateOf(Date()) }
    var selectData by remember { mutableStateOf<String?>(null) }
    var startTime by remember { mutableStateOf<String?>(null) }
    var endTime by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
    var checked by remember { mutableStateOf(false) }
    val gradient = Brush.linearGradient(
        colors = listOf(Color(0xff40A0F5), Color(0xff085BA6))
    )
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    val state = rememberTimePickerState()
    if (showStartTimePicker) {
        TimePickerDialog(
            onCancel = { showStartTimePicker = false },
            onConfirm = {
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, state.hour)
                cal.set(Calendar.MINUTE, state.minute)
                cal.isLenient = false
                startTime = String.format("%02d:%02d", state.hour, state.minute)
                showStartTimePicker = false
            },
        ) {
            TimePicker(state = state)
        }
    }
    if (showEndTimePicker) {
        TimePickerDialog(
            onCancel = { showEndTimePicker = false },
            onConfirm = {
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, state.hour)
                cal.set(Calendar.MINUTE, state.minute)
                cal.isLenient = false
                endTime = String.format("%02d:%02d", state.hour, state.minute)
                showEndTimePicker = false
            },
        ) {
            TimePicker(state = state)
        }
    }
    fun showDatePicker() {
        val datePickerDialog = android.app.DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                // Update the selected date when a date is picked
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                selectedCalenderData = calendar.time
                // Format the selected date as "dd-MMM-yyyy"
                selectData = dateFormat.format(selectedCalenderData)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        text = {
            Column {
                //event name
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                ) {
                    Column {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xfffafafa),
                            shadowElevation = 3.dp,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth(),
                            border = BorderStroke(1.dp, Color(0xffb5b5b5))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)

                            ) {
                                TextField(
                                    value = eventTitle,
                                    onValueChange = { newText ->
                                        eventTitle = newText
                                    },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.Transparent),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Ascii
                                    ),
                                    textStyle = TextStyle(
                                        fontSize = 16.sp
                                    ),
                                    placeholder = {
                                        Text(
                                            text = "Event Name*",
                                            color = Color(0xff565353),
                                            fontSize = 16.sp,
                                        )
                                    },
                                    colors = TextFieldDefaults.textFieldColors(
                                        containerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent,
                                        errorIndicatorColor = Color.Transparent
                                    )
                                )
                            }
                        }
                    }

                }
                Spacer(modifier = Modifier.height(4.dp))
                //description box
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xfffafafa),
                            shadowElevation = 3.dp,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth(),
                            border = BorderStroke(1.dp, Color(0xffb5b5b5))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)

                            ) {
                                TextField(
                                    value = eventDescriptor,
                                    onValueChange = { newText ->
                                        eventDescriptor = newText
                                    },
                                    singleLine = false,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp)
                                        .background(Color.Transparent),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Ascii
                                    ),
                                    textStyle = TextStyle(
                                        fontSize = 16.sp
                                    ),

                                    placeholder = {
                                        Text(
                                            text = "Type the note here ...",
                                            color = Color(0xff565353),
                                            fontSize = 16.sp,
                                            textAlign = TextAlign.Start
                                        )
                                    },
                                    colors = TextFieldDefaults.textFieldColors(
                                        containerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent,
                                        errorIndicatorColor = Color.Transparent
                                    )
                                )
                            }
                        }
                    }


                }
                Spacer(modifier = Modifier.height(4.dp))
                // calender book
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xfffafafa),
                            shadowElevation = 3.dp,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth(),
                            border = BorderStroke(1.dp, Color(0xffb5b5b5))
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    start = 14.dp, top = 10.dp, bottom = 10.dp, end = 10.dp
                                ),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectData ?: "Date",
                                    fontSize = 16.sp,
                                    color = Color(0xff565353),
                                    modifier = Modifier
                                        .weight(3f)
                                        .align(Alignment.CenterVertically)
                                )
                                Surface(
                                    color = Color(0xfff5f5f5),
                                    shape = RoundedCornerShape(10.dp),
                                    shadowElevation = 0.dp,
                                    modifier = Modifier
                                        .weight(.8f)
                                        .fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(8.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.baseline_calendar_month_24),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(25.dp)
                                                .align(Alignment.CenterVertically)
                                                .weight(1f)
                                                .clickable { showDatePicker() },
                                            tint = Color(0xff2c87d9)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                //reminder
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, end = 10.dp)
                ) {
                    Text(
                        text = "Reminds me",
                        fontSize = 16.sp,
                        color = Color(0xff565353),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )

                    Switch(checked = checked, onCheckedChange = {
                        checked = it
                    }, thumbContent = if (checked) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                tint = Color.White,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    } else {
                        null
                    }, colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                        uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                        uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                    ), modifier = Modifier.align(Alignment.CenterVertically))
                }
                //time frame
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xfffafafa),
                            shadowElevation = 3.dp,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth(),
                            border = BorderStroke(1.dp, Color(0xffb5b5b5))
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    start = 14.dp, top = 16.dp, bottom = 16.dp, end = 10.dp
                                ),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = startTime ?: "Start time",
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                    color = Color(0xff565353),
                                    modifier = Modifier
                                        .weight(3f)
                                        .align(Alignment.CenterVertically)
                                )
                                Icon(
                                    painter = painterResource(R.drawable.baseline_access_time_24),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(25.dp)
                                        .align(Alignment.CenterVertically)
                                        .weight(1f)
                                        .clickable { showStartTimePicker = true },
                                    tint = Color(0xff2c87d9)
                                )

                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xfffafafa),
                            shadowElevation = 3.dp,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth(),
                            border = BorderStroke(1.dp, Color(0xffb5b5b5))
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    start = 14.dp, top = 16.dp, bottom = 16.dp, end = 10.dp
                                ),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = endTime ?: "End time",
                                    fontSize = 16.sp,
                                    color = Color(0xff565353),
                                    maxLines = 1,
                                    modifier = Modifier
                                        .weight(3f)
                                        .align(Alignment.CenterVertically)
                                )
                                Icon(
                                    painter = painterResource(R.drawable.baseline_access_time_24),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(25.dp)
                                        .align(Alignment.CenterVertically)
                                        .weight(1f)
                                        .clickable { showEndTimePicker = true },
                                    tint = Color(0xff2c87d9)
                                )

                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = gradient, shape = RoundedCornerShape(10.dp))
            ) {
                Button(
                    onClick = {
                        val newEvent = startTime?.let {
                            endTime?.let { it1 ->
                                Event(
                                    System.currentTimeMillis(),
                                    eventTitle,
                                    eventDescriptor,
                                    selectedCalenderData,
                                    checked,
                                    it,
                                    it1
                                )
                            }
                        }
                        if (newEvent != null) {
                            onCreate(newEvent)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent, contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text(
                        text = "Create Event",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(8.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        },
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