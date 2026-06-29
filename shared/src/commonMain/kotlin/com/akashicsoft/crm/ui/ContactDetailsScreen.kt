package com.akashicsoft.crm.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.akashicsoft.crm.model.Contact
import com.akashicsoft.crm.platform.rememberEmailSender
import com.akashicsoft.crm.platform.rememberMessageSender
import com.akashicsoft.crm.platform.rememberPhoneCaller
import com.akashicsoft.crm.platform.rememberWhatsAppSender
import com.akashicsoft.crm.platform.rememberContactSharer
import com.akashicsoft.crm.ui.components.CrmTopAppBar
import com.akashicsoft.crm.viewModel.ContactDetailViewModel

private val PrimaryPurple = Color(0xFF6E40FF)
private val BackgroundGray = Color(0xFFF8F9FE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailsScreen(
    viewModel: ContactDetailViewModel,
    contactId: String,
    onNavigateBack: () -> Unit,
    onEditClick: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val caller = rememberPhoneCaller()
    val messageSender = rememberMessageSender()
    val emailSender = rememberEmailSender()
    val whatsAppSender = rememberWhatsAppSender()
    val contactSharer = rememberContactSharer()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(contactId) {
        viewModel.loadContact(contactId)
    }

    Scaffold(
        topBar = {
            CrmTopAppBar(
                title = "Contact Details",
                isSubScreen = true,
                onMenuClick = onNavigateBack,
                actions = {
                    IconButton(onClick = { onEditClick(contactId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            state.contact?.let { contact ->
                ContactDetailsBottomBar(
                    isFavorite = contact.isFavorite,
                    onFavoriteToggle = { viewModel.toggleFavorite(contact.id) },
                    onShareClick = { contactSharer.shareContact(contact) },
                    onDeleteClick = { showDeleteDialog = true }
                )
            }
        }
    ) { innerPadding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryPurple)
            }
        } else if (state.contact != null) {
            val contact = state.contact!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(BackgroundGray)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Header Section
                ProfileHeader(contact)
                
                Spacer(Modifier.height(24.dp))

                // Action Buttons Row
                ActionRow(
                    onCallClick = { caller.call(contact.mobileNumber) },
                    onMessageClick = { messageSender.sendMessage(contact.mobileNumber) },
                    onEmailClick = { emailSender.sendEmail(contact.email) },
                    onWhatsAppClick = { whatsAppSender.launchWhatsApp(contact.mobileNumber) }
                )

                Spacer(Modifier.height(24.dp))

                // Contact Information
                InfoSection(
                    title = "Contact Information",
                    items = listOf(
                        InfoItem(Icons.Default.Email, "Email", contact.email),
                        InfoItem(Icons.Default.Call, "Mobile Number", contact.mobileNumber),
                        InfoItem(Icons.Default.Phone, "Landline Number", contact.landlineNumber ?: "N/A"),
                        InfoItem(Icons.Default.Business, "Organization", contact.organization),
                        InfoItem(Icons.Default.Groups, "Department", contact.department ?: "N/A"),
                        InfoItem(Icons.Default.Badge, "Designation", contact.designation)
                    )
                )

                Spacer(Modifier.height(16.dp))

                // Additional Information
                InfoSection(
                    title = "Additional Information",
                    items = listOf(
                        InfoItem(Icons.Default.Tag, "Reference ID", contact.contactNo),
                        InfoItem(Icons.Default.Language, "Source", contact.source)
                    )
                )

                Spacer(Modifier.height(16.dp))

                // Recent Activity Placeholder
                InfoSection(
                    title = "Recent Activity",
                    items = emptyList() // Design shows empty/placeholder
                )
                
                Spacer(Modifier.height(80.dp))
            }
        }

        if (showDeleteDialog && state.contact != null) {
            DeleteConfirmationDialog(
                onConfirm = {
                    showDeleteDialog = false
                    viewModel.deleteContact(state.contact!!.id, onNavigateBack)
                },
                onDismiss = { showDeleteDialog = false }
            )
        }
    }
}

@Composable
private fun ContactDetailsBottomBar(
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onShareClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Surface(
        shadowElevation = 16.dp,
        color = Color.White
    ) {
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 0.dp
        ) {
            NavigationBarItem(
                selected = false,
                onClick = onFavoriteToggle,
                icon = {
                    Icon(
                        if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFFFB400) else Color.Gray
                    )
                },
                label = { Text("Favorite") }
            )
            NavigationBarItem(
                selected = false,
                onClick = onShareClick,
                icon = {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.Gray
                    )
                },
                label = { Text("Share") }
            )
            NavigationBarItem(
                selected = false,
                onClick = onDeleteClick,
                icon = {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red
                    )
                },
                label = { Text("Delete") }
            )
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Contact") },
        text = { Text("Are you sure you want to delete this contact? This action cannot be undone.") },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ProfileHeader(contact: Contact) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryPurple)
            .padding(top = 24.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        contact.initials,
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Icon(
                Icons.Default.Star,
                null,
                tint = Color(0xFFFFB400),
                modifier = Modifier.size(24.dp).padding(4.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            contact.name,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            contact.designation,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp
        )
        Text(
            contact.organization,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp
        )
    }
}

@Composable
private fun ActionRow(
    onCallClick: () -> Unit,
    onMessageClick: () -> Unit,
    onEmailClick: () -> Unit,
    onWhatsAppClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        QuickAction(Icons.Default.Call, "Call", onClick = onCallClick)
        QuickAction(Icons.Default.Email, "Email", onClick = onEmailClick)
        QuickAction(Icons.AutoMirrored.Filled.Message, "Message", onClick = onMessageClick)
        QuickAction(Icons.Default.Textsms, "WhatsApp", onClick = onWhatsAppClick)
    }
}

@Composable
private fun QuickAction(icon: ImageVector, label: String, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            modifier = Modifier.size(50.dp),
            shape = CircleShape,
            color = PrimaryPurple.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = PrimaryPurple)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
private fun InfoSection(title: String, items: List<InfoItem>) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Black
            )
            Spacer(Modifier.height(16.dp))
            if (items.isEmpty()) {
                Text("No activity recorded yet.", color = Color.Gray, fontSize = 12.sp)
            } else {
                items.forEachIndexed { index, item ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            item.icon,
                            null,
                            tint = PrimaryPurple,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(item.label, color = Color.Gray, fontSize = 11.sp)
                            Text(item.value, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        }
                    }
                    if (index < items.size - 1) {
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

private data class InfoItem(val icon: ImageVector, val label: String, val value: String)
