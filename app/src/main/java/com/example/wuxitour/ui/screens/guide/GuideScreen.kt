package com.example.wuxitour.ui.screens.guide

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wuxitour.data.model.Attraction

/**
 * 语音导览页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideScreen() {
    var isPlaying by remember { mutableStateOf(false) }
    var currentGuide by remember { mutableStateOf<GuideItem?>(null) }
    
    val guideItems = remember {
        listOf(
            GuideItem(
                id = "1",
                title = "鼋头渚景区导览",
                description = "太湖佳绝处，毕竟在鼋头",
                duration = "15分钟",
                language = "中文",
                category = "自然景观"
            ),
            GuideItem(
                id = "2",
                title = "灵山大佛文化讲解",
                description = "了解佛教文化和大佛建造历史",
                duration = "20分钟",
                language = "中文",
                category = "人文景观"
            ),
            GuideItem(
                id = "3",
                title = "拈花湾禅意小镇",
                description = "体验东方禅意文化",
                duration = "12分钟",
                language = "中文",
                category = "文化体验"
            ),
            GuideItem(
                id = "4",
                title = "蠡园历史故事",
                description = "西施传说与江南园林",
                duration = "10分钟",
                language = "中文",
                category = "历史文化"
            ),
            GuideItem(
                id = "5",
                title = "锡惠公园古迹",
                description = "无锡最古老的园林景观",
                duration = "18分钟",
                language = "中文",
                category = "历史文化"
            )
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 标题
        Text(
            text = "语音导览",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 当前播放控制器
        if (currentGuide != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "正在播放",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = currentGuide!!.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 进度条
                    LinearProgressIndicator(
                        progress = 0.3f, // 模拟播放进度
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "03:45 / ${currentGuide!!.duration}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        
                        Row {
                            IconButton(
                                onClick = { /* TODO: 上一个 */ }
                            ) {
                                Icon(
                                    Icons.Default.SkipPrevious,
                                    contentDescription = "上一个",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            
                            IconButton(
                                onClick = { isPlaying = !isPlaying }
                            ) {
                                Icon(
                                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "暂停" else "播放",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            
                            IconButton(
                                onClick = { /* TODO: 下一个 */ }
                            ) {
                                Icon(
                                    Icons.Default.SkipNext,
                                    contentDescription = "下一个",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            
                            IconButton(
                                onClick = { 
                                    currentGuide = null
                                    isPlaying = false
                                }
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "关闭",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // 导览列表
        Text(
            text = "可用导览",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(guideItems) { guide ->
                GuideItemCard(
                    guide = guide,
                    isPlaying = currentGuide?.id == guide.id && isPlaying,
                    onPlay = {
                        currentGuide = guide
                        isPlaying = true
                    },
                    onPause = {
                        isPlaying = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideItemCard(
    guide: GuideItem,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 播放按钮
            IconButton(
                onClick = if (isPlaying) onPause else onPlay,
                modifier = Modifier.size(48.dp)
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "暂停" else "播放",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 导览信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = guide.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = guide.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = guide.category,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = "时长",
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = guide.duration,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Icon(
                        Icons.Default.Language,
                        contentDescription = "语言",
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = guide.language,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // 下载按钮
            IconButton(
                onClick = { /* TODO: 下载导览 */ }
            ) {
                Icon(
                    Icons.Default.Download,
                    contentDescription = "下载",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * 导览项数据类
 */
data class GuideItem(
    val id: String,
    val title: String,
    val description: String,
    val duration: String,
    val language: String,
    val category: String
)

