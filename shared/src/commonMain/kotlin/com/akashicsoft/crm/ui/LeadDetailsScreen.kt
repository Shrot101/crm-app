package com.akashicsoft.crm.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
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
import com.akashicsoft.crm.data.local.LeadListUIHelpers
import com.akashicsoft.crm.model.LeadListItem
import com.akashicsoft.crm.ui.components.CrmTopAppBar
import com.akashicsoft.crm.viewModel.LeadDetailsViewModel

@Composable
fun LeadDetailsScreen(
    viewModel: LeadDetailsViewModel,
    leadId: String,
    onNavigateBack: () -> Unit,
    onEditLead: (String) -> Unit // New parameter
) {
    val leadState by viewModel.lead.collectAsState()
    val scrollState = rememberScrollState()
    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(leadId) {
        viewModel.loadLead(leadId)
    }

    Scaffold(
        topBar = {
            CrmTopAppBar(
                title = "Lead Details",
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
                                    onEditLead(leadId)
                                },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            leadState?.let { lead ->
                LeadDetailsBottomActions(
                    lead = lead,
                    onCall = { viewModel.onCallClicked(it) },
                    onEmail = { viewModel.onEmailClicked(it) },
                    onWhatsApp = { viewModel.onWhatsAppClicked(it) }
                )
            }
        },
        containerColor = Color(0xFFF8F9FA) // Light grey background for premium feel
    ) { padding ->
        leadState?.let { lead ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile Header Card
                LeadProfileHeader(lead)

                // Contact Section Card
                InfoGroupCard(title = "Contact Information", icon = Icons.Default.ContactPage) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        lead.emails?.forEach { email ->
                            InfoItem(
                                icon = Icons.Default.Email,
                                label = email.type ?: "Email",
                                value = email.email,
                                isPrimary = email.isPrimary
                            )
                        }
                        lead.phones?.forEach { phone ->
                            InfoItem(
                                icon = if (phone.type == "Landline") Icons.Default.Phone else Icons.Default.Smartphone,
                                label = phone.type ?: "Phone",
                                value = "${phone.countryCode ?: ""} ${phone.phone}",
                                isPrimary = phone.isPrimary
                            )
                        }
                    }
                }

                // Lead Information Card
                InfoGroupCard(title = "Lead Information", icon = Icons.Default.Info) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        InfoItem(icon = Icons.Default.Business, label = "Organization", value = lead.organizationName ?: "Not Specified")
                        InfoItem(icon = Icons.Default.Badge, label = "Designation", value = lead.designation ?: "Not Specified")
                        InfoItem(icon = Icons.Default.Source, label = "Lead Source", value = lead.leadSource ?: "Others")
                        InfoItem(icon = Icons.Default.Tag, label = "Lead Number", value = lead.leadNumber ?: "N/A")
                        InfoItem(icon = Icons.Default.CalendarToday, label = "Created Date", value = lead.createdAt?.take(10) ?: "Unknown")
                    }
                }

                // Ownership Card
                InfoGroupCard(title = "Ownership", icon = Icons.Default.AssignmentInd) {
                    InfoItem(
                        icon = Icons.Default.Person,
                        label = "Assigned To",
                        value = lead.getOwnerName()
                    )
                }

                // Description Section
                InfoGroupCard(title = "Description", icon = Icons.Default.Description) {
                    Text(
                        text = lead.notes?.ifBlank { "No description available." } ?: "No description available.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF6200EE))
        }
    }
}

@Composable
fun LeadProfileHeader(lead: LeadListItem) {
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
            // Stylized Avatar Circle
            Surface(
                modifier = Modifier.size(60.dp),
                shape = CircleShape,
                color = Color(0xFF6200EE).copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = lead.firstName?.take(1)?.uppercase() ?: "?",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF6200EE),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lead.getFullName(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                val statusColor = LeadListUIHelpers.getStatusColor(lead.status)
                Surface(
                    modifier = Modifier.padding(top = 4.dp),
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = lead.status ?: "UNKNOWN",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun InfoGroupCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = Color(0xFF6200EE), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))
            content()
        }
    }
}

@Composable
fun InfoItem(
    icon: ImageVector,
    label: String,
    value: String,
    isPrimary: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Color.LightGray, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                if (isPrimary) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "PRIMARY",
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF4CAF50),
                            fontSize = 8.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LeadDetailsBottomActions(
    lead: LeadListItem,
    onCall: (String) -> Unit,
    onEmail: (String) -> Unit,
    onWhatsApp: (String) -> Unit
) {
    Surface(
        shadowElevation = 16.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionButton(
                icon = Icons.Default.Call,
                label = "Call",
                containerColor = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            ) {
                lead.getPrimaryPhone().let { if (it != "No Phone") onCall(it) }
            }
            ActionButton(
                icon = Icons.Default.Email,
                label = "Email",
                containerColor = Color(0xFF2196F3),
                modifier = Modifier.weight(1f)
            ) {
                lead.getPrimaryEmail().let { if (it != "No Email") onEmail(it) }
            }
            ActionButton(
                icon = Icons.AutoMirrored.Filled.Message, // Placeholder for WhatsApp
                label = "WhatsApp",
                containerColor = Color(0xFF25D366),
                modifier = Modifier.weight(1f)
            ) {
                lead.getPrimaryPhone().let { if (it != "No Phone") onWhatsApp(it) }
            }
        }
    }
}

@Composable
fun ActionButton(
    icon: ImageVector,
    label: String,
    containerColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        Icon(icon, null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}
