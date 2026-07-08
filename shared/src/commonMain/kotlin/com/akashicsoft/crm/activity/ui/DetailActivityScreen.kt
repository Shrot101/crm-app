package com.akashicsoft.crm.activity.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akashicsoft.crm.activity.model.Activity
import com.akashicsoft.crm.activity.model.ActivityPriority
import com.akashicsoft.crm.activity.model.ActivityType
import com.akashicsoft.crm.activity.viewmodel.DetailActivityViewModel
import com.akashicsoft.crm.ui.InfoGroupCard
import com.akashicsoft.crm.ui.InfoItem
import com.akashicsoft.crm.ui.components.CrmTopAppBar

@Composable
fun DetailActivityScreen(
    viewModel: DetailActivityViewModel,
    activityId: String,
    onNavigateBack: () -> Unit,
    onEditActivity: (String) -> Unit,
) {
    val activityState by viewModel.activity.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val scrollState = rememberScrollState()
    var showMenu by remember { mutableStateOf(value = false) }

    LaunchedEffect(activityId) {
        viewModel.loadActivity(activityId)
    }

    Scaffold(
        topBar = {
            CrmTopAppBar(
                title = "Activity Details",
                isSubScreen = true,
                onMenuClick = onNavigateBack,
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                onClick = {
                                    showMenu = false
                                    onEditActivity(activityId)
                                },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = {
                                    showMenu = false
                                    viewModel.deleteActivity(activityId) {
                                        onNavigateBack()
                                    }
                                },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) }
                            )
                        }
                    }
                }
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF6200EE))
            }
        } else {
            activityState?.let { activity ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header Card
                    ActivityHeaderCard(activity)

                    // Schedule Section
                    InfoGroupCard(title = "Schedule", icon = Icons.Default.CalendarToday) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            InfoItem(
                                icon = Icons.Default.Event,
                                label = "Start Date",
                                value = activity.date.toString()
                            )
                            InfoItem(
                                icon = Icons.Default.AccessTime,
                                label = "Start Time",
                                value = activity.time
                            )
                            activity.endDate?.let {
                                InfoItem(
                                    icon = Icons.Default.Event,
                                    label = "End Date",
                                    value = it.toString()
                                )
                            }
                            activity.endTime?.let {
                                InfoItem(
                                    icon = Icons.Default.AccessTime,
                                    label = "End Time",
                                    value = it
                                )
                            }
                        }
                    }

                    // Classification Section
                    InfoGroupCard(title = "Classification", icon = Icons.Default.Category) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            InfoItem(
                                icon = Icons.AutoMirrored.Filled.FormatListBulleted,
                                label = "Type",
                                value = activity.type.name.lowercase().replaceFirstChar { it.uppercase() }
                            )
                            activity.source?.let {
                                InfoItem(
                                    icon = Icons.Default.Source,
                                    label = "Source",
                                    value = it.name.lowercase().replaceFirstChar { it.uppercase() }
                                )
                            }
                            activity.priority?.let {
                                InfoItem(
                                    icon = Icons.Default.PriorityHigh,
                                    label = "Priority",
                                    value = it.name.lowercase().replaceFirstChar { it.uppercase() }
                                )
                            }
                        }
                    }

                    // Relationships Section
                    InfoGroupCard(title = "Relationships", icon = Icons.Default.People) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            InfoItem(
                                icon = Icons.Default.Person,
                                label = "Contact",
                                value = activity.contact?.name ?: "Not Specified"
                            )
                            InfoItem(
                                icon = Icons.Default.Business,
                                label = "Organization",
                                value = activity.organization ?: "Not Specified"
                            )
                            InfoItem(
                                icon = Icons.Default.Handshake,
                                label = "Deal",
                                value = activity.deal?.title ?: "Not Specified"
                            )
                        }
                    }

                    // Additional Info Section
                    InfoGroupCard(title = "Additional Info", icon = Icons.Default.Info) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            InfoItem(
                                icon = Icons.Default.Place,
                                label = "Location",
                                value = activity.location ?: "Not Specified"
                            )
                            InfoItem(
                                icon = Icons.Default.Notifications,
                                label = "Reminder",
                                value = activity.reminder ?: "None"
                            )
                            InfoItem(
                                icon = Icons.AutoMirrored.Filled.List,
                                label = "Agenda",
                                value = activity.agenda ?: "Not Specified"
                            )
                        }
                    }

                    // Assignment & Notes Section
                    InfoGroupCard(title = "Assignment & Notes", icon = Icons.Default.AssignmentInd) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            InfoItem(
                                icon = Icons.Default.Inventory2,
                                label = "Product/Service",
                                value = activity.product ?: "Not Specified"
                            )
                            InfoItem(
                                icon = Icons.Default.PersonOutline,
                                label = "Assigned To",
                                value = activity.assignedTo?.name ?: "Not Specified"
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "NOTES",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = activity.description.ifBlank { "No notes provided." },
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Activity not found")
            }
        }
    }
}

@Composable
fun ActivityHeaderCard(activity: Activity) {
    val (icon, color) = when (activity.type) {
        ActivityType.CALL -> Icons.Default.Call to Color(0xFF27AE60)
        ActivityType.MESSAGE -> Icons.AutoMirrored.Filled.Message to Color(0xFF2D9CDB)
        ActivityType.WHATSAPP -> Icons.AutoMirrored.Outlined.Chat to Color(0xFF25D366)
        ActivityType.MEETING -> Icons.Default.Groups to Color(0xFF6E40FF)
        ActivityType.EVENTS -> Icons.Default.Event to Color(0xFFF2994A)
    }

    val priorityColor = when (activity.priority) {
        ActivityPriority.LOW -> Color.Gray
        ActivityPriority.NORMAL -> Color(0xFF4CAF50)
        ActivityPriority.HIGH -> Color(0xFFF2994A)
        ActivityPriority.URGENT -> Color.Red
        null -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(60.dp),
                shape = CircleShape,
                color = color.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = color.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = activity.type.name,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = color,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    activity.priority?.let {
                        Surface(
                            color = priorityColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = it.name,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = priorityColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
