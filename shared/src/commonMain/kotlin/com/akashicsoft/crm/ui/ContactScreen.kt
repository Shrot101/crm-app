package com.akashicsoft.crm.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akashicsoft.crm.model.Contact
import com.akashicsoft.crm.platform.rememberEmailSender
import com.akashicsoft.crm.platform.rememberMessageSender
import com.akashicsoft.crm.platform.rememberPhoneCaller
import com.akashicsoft.crm.ui.components.LeadListSkeleton
import com.akashicsoft.crm.viewModel.ContactViewModel
import kotlinx.coroutines.launch

private val PrimaryPurple = Color(0xFF6E40FF)
private val BackgroundGray = Color(0xFFF8F9FE)
private val SidebarGreen = Color(0xFF27AE60)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(
    viewModel: ContactViewModel,
    modifier: Modifier = Modifier,
    onContactSelected: (String) -> Unit = {},
    onAddContactClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    var expandedContactId by remember { mutableStateOf<String?>(null) }
    val caller = rememberPhoneCaller()
    val messageSender = rememberMessageSender()
    val emailSender = rememberEmailSender()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    val alphabet = remember { ('A'..'Z').toList() + '#' }
    var selectedLetter by remember { mutableStateOf<Char?>(null) }
    var isDragging by remember { mutableStateOf(false) }
    var isScrolling by remember { mutableStateOf(false) }

    // Logic to detect if the list is being scrolled
    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            isScrolling = true
        } else if (!isDragging) {
            // Delay hiding the scrollbar a bit after scrolling stops
            kotlinx.coroutines.delay(1500)
            isScrolling = false
        }
    }

    Box(modifier = modifier.fillMaxSize().background(BackgroundGray)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Content
            if (state.isLoading) {
                LeadListSkeleton(modifier = Modifier.weight(1f))
            } else {
                Box(modifier = Modifier.weight(1f)) {
                    val sortedKeys = remember(state.groupedContacts) {
                        state.groupedContacts.keys.sorted()
                    }

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val favoriteContacts = state.contacts.filter { it.isFavorite }
                        if (favoriteContacts.isNotEmpty()) {
                            item {
                                AlphabeticalContactCard(
                                    letter = '★', // Using star for favorite section
                                    contacts = favoriteContacts,
                                    expandedContactId = expandedContactId,
                                    onContactExpandToggle = { id ->
                                        expandedContactId = if (expandedContactId == id) null else id
                                    },
                                    onInfoClick = onContactSelected,
                                    onCallClick = { caller.call(it) },
                                    onMessageClick = { messageSender.sendMessage(it) },
                                    onEmailClick = { emailSender.sendEmail(it) },
                                    isFavoriteSection = true
                                )
                            }
                        }

                        items(
                            items = sortedKeys,
                            key = { it }
                        ) { letter ->
                            val contacts = state.groupedContacts[letter] ?: emptyList()
                            AlphabeticalContactCard(
                                letter = letter,
                                contacts = contacts,
                                expandedContactId = expandedContactId,
                                onContactExpandToggle = { id ->
                                    expandedContactId = if (expandedContactId == id) null else id
                                },
                                onInfoClick = onContactSelected,
                            onCallClick = { caller.call(it) },
                            onMessageClick = { messageSender.sendMessage(it) },
                            onEmailClick = { emailSender.sendEmail(it) }
                        )
                        }
                    }

                    // Sidebar Index
                    androidx.compose.animation.AnimatedVisibility(
                        visible = isDragging || isScrolling,
                        enter = fadeIn(),
                        exit = fadeOut(),
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        AlphabeticalIndexBar(
                            alphabet = alphabet,
                            onLetterSelected = { letter ->
                                selectedLetter = letter
                                val index = sortedKeys.indexOf(letter)
                                if (index != -1) {
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(index + 1)
                                    }
                                }
                            },
                            onDraggingChanged = { 
                                isDragging = it 
                                if (!it) {
                                    // When dragging stops, we might want to start the hide timer
                                    isScrolling = true 
                                }
                            }
                        )
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick          = onAddContactClick,
            modifier         = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor   = Color(0xFF6200EE),
            contentColor     = Color.White,
            shape            = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector        = Icons.Default.Add,
                contentDescription = "Add Contact"
            )
        }

        // Letter Popup Overlay
        AnimatedVisibility(
            visible = isDragging && selectedLetter != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = SidebarGreen,
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = selectedLetter?.toString() ?: "",
                        color = Color.White,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun AlphabeticalIndexBar(
    alphabet: List<Char>,
    modifier: Modifier = Modifier,
    onLetterSelected: (Char) -> Unit,
    onDraggingChanged: (Boolean) -> Unit
) {
    var barHeight by remember { mutableStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(40.dp) // Wider touch area
            .padding(vertical = 24.dp, horizontal = 4.dp)
            .onGloballyPositioned { barHeight = it.size.height }
            .pointerInput(alphabet, barHeight) {
                fun handleTouch(y: Float) {
                    val sectionHeight = barHeight.toFloat() / alphabet.size
                    val index = (y / sectionHeight).toInt().coerceIn(0, alphabet.size - 1)
                    onLetterSelected(alphabet[index])
                }

                detectDragGestures(
                    onDragStart = { offset ->
                        onDraggingChanged(true)
                        handleTouch(offset.y)
                    },
                    onDragEnd = { onDraggingChanged(false) },
                    onDragCancel = { onDraggingChanged(false) },
                    onDrag = { change, _ ->
                        handleTouch(change.position.y)
                    }
                )
            }
            .pointerInput(alphabet, barHeight) {
                detectTapGestures { offset ->
                    val sectionHeight = barHeight.toFloat() / alphabet.size
                    val index = (offset.y / sectionHeight).toInt().coerceIn(0, alphabet.size - 1)
                    onLetterSelected(alphabet[index])
                }
            },
        contentAlignment = Alignment.CenterEnd
    ) {
        // The actual scrollbar track with letters inside
        Surface(
            modifier = Modifier
                .width(16.dp) // Slightly wider track to house letters
                .fillMaxHeight()
                .clip(RoundedCornerShape(8.dp)),
            color = Color.LightGray.copy(alpha = 0.3f)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                alphabet.forEach { letter ->
                    Text(
                        text = letter.toString(),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray.copy(alpha = 0.8f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun AlphabeticalContactCard(
    letter: Char,
    contacts: List<Contact>,
    expandedContactId: String?,
    onContactExpandToggle: (String) -> Unit,
    onInfoClick: (String) -> Unit,
    onCallClick: (String) -> Unit,
    onMessageClick: (String) -> Unit,
    onEmailClick: (String) -> Unit,
    isFavoriteSection: Boolean = false
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, start = 12.dp), // Space for the floating letter
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                // Contact List for this letter
                contacts.forEachIndexed { index, contact ->
                    ContactListItem(
                        contact = contact,
                        isExpanded = expandedContactId == contact.id,
                        onExpandToggle = { onContactExpandToggle(contact.id) },
                        onInfoClick = { onInfoClick(contact.id) },
                        onCallClick = { onCallClick(contact.mobileNumber) },
                        onMessageClick = { onMessageClick(contact.mobileNumber) },
                        onEmailClick = { onEmailClick(contact.email) }
                    )
                    if (index < contacts.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = Color(0xFFF0F0F0)
                        )
                    }
                }
            }
        }

        // Letter or Star over the top-left corner
        Surface(
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            color = if (isFavoriteSection) Color(0xFFFFB400) else PrimaryPurple,
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isFavoriteSection) {
                    Icon(
                        Icons.Default.Star,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text(
                        text = letter.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactListItem(
    contact: Contact,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    onInfoClick: () -> Unit,
    onCallClick: () -> Unit,
    onMessageClick: () -> Unit,
    onEmailClick: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    val animatedOffsetX by animateFloatAsState(targetValue = offsetX)
    val coroutineScope = rememberCoroutineScope()

    // Background color and labels based on swipe direction
    val backgroundColor = when {
        offsetX > 0 -> Color(0xFF27AE60) // Green for Call (Right swipe)
        offsetX < 0 -> Color(0xFF2D9CDB) // Blue for Message (Left swipe)
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(backgroundColor)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        val width = size.width
                        if (offsetX > width / 2) {
                            // Confirmed Call
                            offsetX = width.toFloat()
                            onCallClick()
                            coroutineScope.launch {
                                kotlinx.coroutines.delay(2000)
                                offsetX = 0f
                            }
                        } else if (offsetX < -width / 2) {
                            // Confirmed Message
                            offsetX = -width.toFloat()
                            onMessageClick()
                            coroutineScope.launch {
                                kotlinx.coroutines.delay(2000)
                                offsetX = 0f
                            }
                        } else {
                            // Reset
                            coroutineScope.launch {
                                offsetX = 0f
                            }
                        }
                    },
                    onDragCancel = {
                        offsetX = 0f
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount
                    }
                )
            }
    ) {
        // Background - Left Side (Call)
        if (offsetX > 0) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterStart)
                    .padding(horizontal = 20.dp)
                    .graphicsLayer(alpha = (offsetX / 300f).coerceIn(0f, 1f))
            ) {
                Icon(Icons.Default.Call, contentDescription = "Call", tint = Color.White)
                Text(text = "Call", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        // Background - Right Side (Message)
        if (offsetX < 0) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
                    .padding(horizontal = 20.dp)
                    .graphicsLayer(alpha = (-offsetX / 300f).coerceIn(0f, 1f))
            ) {
                Text(text = "Message", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Icon(Icons.AutoMirrored.Filled.Message, contentDescription = "Message", tint = Color.White)
            }
        }

        // The Contact Card itself
        Column(
            modifier = Modifier
                .offset { IntOffset(animatedOffsetX.toInt(), 0) }
                .fillMaxWidth()
                .background(Color.White)
                .clickable(onClick = onExpandToggle)
                .padding(16.dp)
                .animateContentSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = PrimaryPurple.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            contact.initials,
                            color = PrimaryPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        contact.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        "${contact.designation} • ${contact.organization}",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                if (contact.isFavorite) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFB400), modifier = Modifier.size(18.dp))
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ActionButton(
                            icon = Icons.Default.Call,
                            backgroundColor = Color(0xFF27AE60),
                            onClick = onCallClick
                        )
                        ActionButton(
                            icon = Icons.Default.Email,
                            backgroundColor = Color(0xFFF2C94C),
                            onClick = onEmailClick
                        )
                        ActionButton(
                            icon = Icons.AutoMirrored.Filled.Message,
                            backgroundColor = Color(0xFF2D9CDB),
                            onClick = onMessageClick
                        )
                        ActionButton(
                            icon = Icons.Default.Info,
                            backgroundColor = Color(0xFFBDBDBD),
                            onClick = onInfoClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(44.dp)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = backgroundColor
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                icon,
                null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
