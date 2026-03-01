package land.tbp.augment.cli.session.importer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val sessionId: String,
    val created: String,
    val modified: String,
    val chatHistory: List<ChatHistory>,
    val agentState: AgentState,
    val rootTaskUuid: String,
    val customTitle: String? = null,
    val terminalId: String? = null,
)

@Serializable
data class AgentState(
    val userGuidelines: String,
    val workspaceGuidelines: String,
    val agentMemories: String,
    val modelId: String,
    val userEmail: String,
)

@Serializable
data class ChatHistory(
    val exchange: Exchange,
    val completed: Boolean,
    val sequenceId: Double, // Can be fractional (e.g., 5.5) in some sessions
    val finishedAt: String,
    val changedFiles: List<String>,
    val changedFilesSkipped: List<String>,
    val changedFilesSkippedCount: Int,
    val isHistorySummary: Boolean? = null,
    val historySummaryVersion: Int? = null,
    val source: String? = null,
)

@Serializable
data class Exchange(
    @SerialName("request_message") val requestMessage: String,
    @SerialName("response_text") val responseText: String,
    @SerialName("request_id") val requestId: String,
    @SerialName("request_nodes") val requestNodes: List<RequestNode>,
    @SerialName("response_nodes") val responseNodes: List<ResponseNode>,
)

// ============================================================================
// Request Nodes - Types: 0 (Text), 1 (ToolResult), 4 (IdeState)
// ============================================================================

@Serializable
data class RequestNode(
    val id: Int,
    val type: Int,
    @SerialName("text_node") val textNode: TextNode? = null,
    @SerialName("tool_result_node") val toolResultNode: ToolResultNode? = null,
    @SerialName("ide_state_node") val ideStateNode: IdeStateNode? = null,
)

@Serializable
data class TextNode(
    val content: String,
)

@Serializable
data class ToolResultNode(
    @SerialName("tool_use_id") val toolUseId: String,
    val content: String,
    @SerialName("is_error") val isError: Boolean,
    @SerialName("duration_ms") val durationMs: Long? = null,
    @SerialName("start_time_ms") val startTimeMs: Long? = null,
    @SerialName("request_id") val requestId: String? = null,
    val metadata: ToolResultMetadata? = null,
)

@Serializable
data class ToolResultMetadata(
    @SerialName("tool_lines_added") val toolLinesAdded: Int? = null,
    @SerialName("tool_lines_deleted") val toolLinesDeleted: Int? = null,
)

@Serializable
data class IdeStateNode(
    @SerialName("workspace_folders") val workspaceFolders: List<WorkspaceFolder>,
    @SerialName("workspace_folders_unchanged") val workspaceFoldersUnchanged: Boolean,
    @SerialName("current_terminal") val currentTerminal: CurrentTerminal,
)

@Serializable
data class WorkspaceFolder(
    @SerialName("repository_root") val repositoryRoot: String,
    @SerialName("folder_root") val folderRoot: String,
)

@Serializable
data class CurrentTerminal(
    @SerialName("terminal_id") val terminalId: Int,
    @SerialName("current_working_directory") val currentWorkingDirectory: String,
)

// ============================================================================
// Response Nodes - Types: 0 (Text), 5 (ToolUse), 8 (Thinking), 10 (TokenUsage)
// ============================================================================

@Serializable
data class ResponseNode(
    val id: Int,
    val type: Int,
    val content: String,
    @SerialName("tool_use") val toolUse: ToolUse? = null,
    val thinking: Thinking? = null,
    @SerialName("billing_metadata") val billingMetadata: String? = null,
    val metadata: Metadata? = null,
    @SerialName("token_usage") val tokenUsage: TokenUsage? = null,
    @SerialName("timestamp_ms") val timestampMs: Long? = null,
)

@Serializable
data class ToolUse(
    @SerialName("tool_use_id") val toolUseId: String,
    @SerialName("tool_name") val toolName: String,
    @SerialName("input_json") val inputJson: String,
    @SerialName("is_partial") val isPartial: Boolean,
    @SerialName("started_at_ms") val startedAtMs: Long? = null,
    @SerialName("completed_at_ms") val completedAtMs: Long? = null,
)

@Serializable
data class Thinking(
    val summary: String,
    @SerialName("encrypted_content") val encryptedContent: String,
    val content: String? = null,
    @SerialName("openai_responses_api_item_id") val openaiResponsesApiItemId: String? = null,
)

@Serializable
data class TokenUsage(
    @SerialName("input_tokens") val inputTokens: Long,
    @SerialName("output_tokens") val outputTokens: Long,
    @SerialName("cache_read_input_tokens") val cacheReadInputTokens: Long,
    @SerialName("cache_creation_input_tokens") val cacheCreationInputTokens: Long,
    @SerialName("system_prompt_tokens") val systemPromptTokens: Long,
    @SerialName("chat_history_tokens") val chatHistoryTokens: Long,
    @SerialName("current_message_tokens") val currentMessageTokens: Long,
    @SerialName("max_context_tokens") val maxContextTokens: Long,
    @SerialName("tool_definitions_tokens") val toolDefinitionsTokens: Long? = null,
    @SerialName("tool_result_tokens") val toolResultTokens: Long? = null,
    @SerialName("assistant_response_tokens") val assistantResponseTokens: Long? = null,
)

@Serializable
data class Metadata(
    @SerialName("openai_id") val openaiId: String? = null,
    @SerialName("google_ts") val googleTs: String? = null,
    val provider: String? = null,
    val phase: String? = null,
)
