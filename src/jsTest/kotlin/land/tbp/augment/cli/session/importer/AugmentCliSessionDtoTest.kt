package land.tbp.augment.cli.session.importer

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.json.Json

/**
 * Deserialization tests for AugmentCliSessionDto classes.
 * These tests ensure backwards compatibility when session file formats change.
 */
class AugmentCliSessionDtoTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun shouldDeserializeMinimalSession() {
        val sessionJson = """
        {
            "sessionId": "test-session-id",
            "created": "2026-01-16T12:00:00.000Z",
            "modified": "2026-01-16T12:30:00.000Z",
            "chatHistory": [],
            "agentState": {
                "userGuidelines": "",
                "workspaceGuidelines": "",
                "agentMemories": "",
                "modelId": "claude-opus-4-5",
                "userEmail": "test@example.com"
            },
            "rootTaskUuid": "task-uuid-123"
        }
        """.trimIndent()

        val actual = json.decodeFromString<Session>(sessionJson)

        val expected = Session(
            sessionId = "test-session-id",
            created = "2026-01-16T12:00:00.000Z",
            modified = "2026-01-16T12:30:00.000Z",
            chatHistory = emptyList(),
            agentState = AgentState(
                userGuidelines = "",
                workspaceGuidelines = "",
                agentMemories = "",
                modelId = "claude-opus-4-5",
                userEmail = "test@example.com"
            ),
            rootTaskUuid = "task-uuid-123",
            customTitle = null,
            terminalId = null
        )

        assertEquals(expected, actual)
    }

    @Test
    fun shouldDeserializeSessionWithOptionalFields() {
        val sessionJson = """
        {
            "sessionId": "test-session-id",
            "created": "2026-01-16T12:00:00.000Z",
            "modified": "2026-01-16T12:30:00.000Z",
            "chatHistory": [],
            "agentState": {
                "userGuidelines": "Be helpful",
                "workspaceGuidelines": "Follow coding standards",
                "agentMemories": "User prefers Kotlin",
                "modelId": "claude-opus-4-5",
                "userEmail": "test@example.com"
            },
            "rootTaskUuid": "task-uuid-123",
            "customTitle": "My Custom Session Title",
            "terminalId": "/dev/cons0"
        }
        """.trimIndent()

        val actual = json.decodeFromString<Session>(sessionJson)

        val expected = Session(
            sessionId = "test-session-id",
            created = "2026-01-16T12:00:00.000Z",
            modified = "2026-01-16T12:30:00.000Z",
            chatHistory = emptyList(),
            agentState = AgentState(
                userGuidelines = "Be helpful",
                workspaceGuidelines = "Follow coding standards",
                agentMemories = "User prefers Kotlin",
                modelId = "claude-opus-4-5",
                userEmail = "test@example.com"
            ),
            rootTaskUuid = "task-uuid-123",
            customTitle = "My Custom Session Title",
            terminalId = "/dev/cons0"
        )

        assertEquals(expected, actual)
    }

    @Test
    fun shouldDeserializeChatHistoryWithFractionalSequenceId() {
        val chatHistoryJson = """
        {
            "exchange": {
                "request_message": "test",
                "response_text": "response",
                "request_id": "req-123",
                "request_nodes": [],
                "response_nodes": []
            },
            "completed": true,
            "sequenceId": 5.5,
            "finishedAt": "2026-01-16T12:00:00.000Z",
            "changedFiles": [],
            "changedFilesSkipped": [],
            "changedFilesSkippedCount": 0
        }
        """.trimIndent()

        val actual = json.decodeFromString<ChatHistory>(chatHistoryJson)

        val expected = ChatHistory(
            exchange = Exchange(
                requestMessage = "test",
                responseText = "response",
                requestId = "req-123",
                requestNodes = emptyList(),
                responseNodes = emptyList()
            ),
            completed = true,
            sequenceId = 5.5,
            finishedAt = "2026-01-16T12:00:00.000Z",
            changedFiles = emptyList(),
            changedFilesSkipped = emptyList(),
            changedFilesSkippedCount = 0,
            isHistorySummary = null,
            historySummaryVersion = null,
            source = null
        )

        assertEquals(expected, actual)
    }

    @Test
    fun shouldDeserializeChatHistoryWithOptionalFields() {
        val chatHistoryJson = """
        {
            "exchange": {
                "request_message": "test",
                "response_text": "response",
                "request_id": "req-123",
                "request_nodes": [],
                "response_nodes": []
            },
            "completed": true,
            "sequenceId": 1,
            "finishedAt": "2026-01-16T12:00:00.000Z",
            "changedFiles": ["file1.kt"],
            "changedFilesSkipped": ["file2.kt"],
            "changedFilesSkippedCount": 1,
            "isHistorySummary": false,
            "historySummaryVersion": 0,
            "source": "local"
        }
        """.trimIndent()

        val actual = json.decodeFromString<ChatHistory>(chatHistoryJson)

        val expected = ChatHistory(
            exchange = Exchange(
                requestMessage = "test",
                responseText = "response",
                requestId = "req-123",
                requestNodes = emptyList(),
                responseNodes = emptyList()
            ),
            completed = true,
            sequenceId = 1.0,
            finishedAt = "2026-01-16T12:00:00.000Z",
            changedFiles = listOf("file1.kt"),
            changedFilesSkipped = listOf("file2.kt"),
            changedFilesSkippedCount = 1,
            isHistorySummary = false,
            historySummaryVersion = 0,
            source = "local"
        )

        assertEquals(expected, actual)
    }

    @Test
    fun shouldDeserializeRequestNodeType0TextNode() {
        val requestNodeJson = """
        {
            "id": 1,
            "type": 0,
            "text_node": {
                "content": "Hello, this is the user's message"
            }
        }
        """.trimIndent()

        val actual = json.decodeFromString<RequestNode>(requestNodeJson)

        val expected = RequestNode(
            id = 1,
            type = 0,
            textNode = TextNode(content = "Hello, this is the user's message"),
            toolResultNode = null,
            ideStateNode = null
        )

        assertEquals(expected, actual)
    }

    @Test
    fun shouldDeserializeRequestNodeType1ToolResultNodeMinimal() {
        val requestNodeJson = """
        {
            "id": 1,
            "type": 1,
            "tool_result_node": {
                "tool_use_id": "toolu_vrtx_01ABC123",
                "content": "Here's the file content...",
                "is_error": false
            }
        }
        """.trimIndent()

        val actual = json.decodeFromString<RequestNode>(requestNodeJson)

        val expected = RequestNode(
            id = 1,
            type = 1,
            textNode = null,
            toolResultNode = ToolResultNode(
                toolUseId = "toolu_vrtx_01ABC123",
                content = "Here's the file content...",
                isError = false,
                durationMs = null,
                startTimeMs = null,
                requestId = null,
                metadata = null
            ),
            ideStateNode = null
        )

        assertEquals(expected, actual)
    }

    @Test
    fun shouldDeserializeRequestNodeType1ToolResultNodeWithAllFields() {
        val requestNodeJson = """
        {
            "id": 1,
            "type": 1,
            "tool_result_node": {
                "tool_use_id": "toolu_vrtx_01ABC123",
                "content": "File edited successfully",
                "is_error": false,
                "duration_ms": 410,
                "start_time_ms": 1772197681596,
                "request_id": "req-456",
                "metadata": {
                    "tool_lines_added": 15,
                    "tool_lines_deleted": 3
                }
            }
        }
        """.trimIndent()

        val actual = json.decodeFromString<RequestNode>(requestNodeJson)

        val expected = RequestNode(
            id = 1,
            type = 1,
            textNode = null,
            toolResultNode = ToolResultNode(
                toolUseId = "toolu_vrtx_01ABC123",
                content = "File edited successfully",
                isError = false,
                durationMs = 410,
                startTimeMs = 1772197681596,
                requestId = "req-456",
                metadata = ToolResultMetadata(toolLinesAdded = 15, toolLinesDeleted = 3)
            ),
            ideStateNode = null
        )

        assertEquals(expected, actual)
    }

    @Test
    fun shouldDeserializeRequestNodeType1ToolResultWithError() {
        val requestNodeJson = """
        {
            "id": 1,
            "type": 1,
            "tool_result_node": {
                "tool_use_id": "toolu_vrtx_01XYZ789",
                "content": "Command not found: obsidian",
                "is_error": true,
                "duration_ms": 699,
                "start_time_ms": 1772213615599
            }
        }
        """.trimIndent()

        val actual = json.decodeFromString<RequestNode>(requestNodeJson)

        val expected = RequestNode(
            id = 1,
            type = 1,
            textNode = null,
            toolResultNode = ToolResultNode(
                toolUseId = "toolu_vrtx_01XYZ789",
                content = "Command not found: obsidian",
                isError = true,
                durationMs = 699,
                startTimeMs = 1772213615599,
                requestId = null,
                metadata = null
            ),
            ideStateNode = null
        )

        assertEquals(expected, actual)
    }

    @Test
    fun shouldDeserializeRequestNodeType4IdeStateNode() {
        val requestNodeJson = """
        {
            "id": 2,
            "type": 4,
            "ide_state_node": {
                "workspace_folders": [
                    {
                        "repository_root": "d:\\all\\work\\MyProject",
                        "folder_root": "d:\\all\\work\\MyProject"
                    }
                ],
                "workspace_folders_unchanged": false,
                "current_terminal": {
                    "terminal_id": 0,
                    "current_working_directory": "d:\\all\\work\\MyProject"
                }
            }
        }
        """.trimIndent()

        val actual = json.decodeFromString<RequestNode>(requestNodeJson)

        val expected = RequestNode(
            id = 2,
            type = 4,
            textNode = null,
            toolResultNode = null,
            ideStateNode = IdeStateNode(
                workspaceFolders = listOf(
                    WorkspaceFolder(
                        repositoryRoot = "d:\\all\\work\\MyProject",
                        folderRoot = "d:\\all\\work\\MyProject"
                    )
                ),
                workspaceFoldersUnchanged = false,
                currentTerminal = CurrentTerminal(
                    terminalId = 0,
                    currentWorkingDirectory = "d:\\all\\work\\MyProject"
                )
            )
        )

        assertEquals(expected, actual)
    }

    @Test
    fun shouldDeserializeResponseNodeType0TextContent() {
        val responseNodeJson = """
        {
            "id": 1,
            "type": 0,
            "content": "Here is the response from the LLM",
            "tool_use": null,
            "thinking": null,
            "billing_metadata": null,
            "metadata": {
                "openai_id": null,
                "google_ts": null,
                "provider": "anthropic"
            },
            "token_usage": null,
            "timestamp_ms": 1772197681581
        }
        """.trimIndent()

        val actual = json.decodeFromString<ResponseNode>(responseNodeJson)

        val expected = ResponseNode(
            id = 1,
            type = 0,
            content = "Here is the response from the LLM",
            toolUse = null,
            thinking = null,
            billingMetadata = null,
            metadata = Metadata(openaiId = null, googleTs = null, provider = "anthropic", phase = null),
            tokenUsage = null,
            timestampMs = 1772197681581
        )

        assertEquals(expected, actual)
    }

    @Test
    fun shouldDeserializeResponseNodeType5ToolUseMinimal() {
        val responseNodeJson = """
        {
            "id": 1,
            "type": 5,
            "content": "",
            "tool_use": {
                "tool_use_id": "toolu_vrtx_01ABC123",
                "tool_name": "view",
                "input_json": "{\"path\": \".\", \"type\": \"directory\"}",
                "is_partial": false
            },
            "thinking": null,
            "billing_metadata": null,
            "metadata": null,
            "token_usage": null
        }
        """.trimIndent()

        val actual = json.decodeFromString<ResponseNode>(responseNodeJson)

        val expected = ResponseNode(
            id = 1,
            type = 5,
            content = "",
            toolUse = ToolUse(
                toolUseId = "toolu_vrtx_01ABC123",
                toolName = "view",
                inputJson = """{"path": ".", "type": "directory"}""",
                isPartial = false,
                startedAtMs = null,
                completedAtMs = null
            ),
            thinking = null,
            billingMetadata = null,
            metadata = null,
            tokenUsage = null,
            timestampMs = null
        )

        assertEquals(expected, actual)
    }

    @Test
    fun shouldDeserializeResponseNodeType5ToolUseWithTiming() {
        val responseNodeJson = """
        {
            "id": 1,
            "type": 5,
            "content": "",
            "tool_use": {
                "tool_use_id": "toolu_vrtx_01ABC123",
                "tool_name": "launch-process",
                "input_json": "{\"command\": \"Get-Date\", \"cwd\": \"d:\\\\all\\\\notes\", \"wait\": true}",
                "is_partial": false,
                "started_at_ms": 1772197681596,
                "completed_at_ms": 1772197682006
            },
            "thinking": null,
            "metadata": {
                "provider": null
            }
        }
        """.trimIndent()

        val actual = json.decodeFromString<ResponseNode>(responseNodeJson)

        val expected = ResponseNode(
            id = 1,
            type = 5,
            content = "",
            toolUse = ToolUse(
                toolUseId = "toolu_vrtx_01ABC123",
                toolName = "launch-process",
                inputJson = """{"command": "Get-Date", "cwd": "d:\\all\\notes", "wait": true}""",
                isPartial = false,
                startedAtMs = 1772197681596,
                completedAtMs = 1772197682006
            ),
            thinking = null,
            billingMetadata = null,
            metadata = Metadata(openaiId = null, googleTs = null, provider = null, phase = null),
            tokenUsage = null,
            timestampMs = null
        )

        assertEquals(expected, actual)
    }

    @Test
    fun shouldDeserializeResponseNodeType8Thinking() {
        val responseNodeJson = """
        {
            "id": 0,
            "type": 8,
            "content": "",
            "tool_use": null,
            "thinking": {
                "summary": "The user wants me to see a TODO file.",
                "encrypted_content": "EsECCkgICxACGAIqQKwV9Ej8245MvW4...",
                "content": null,
                "openai_responses_api_item_id": null
            },
            "billing_metadata": null,
            "metadata": {
                "openai_id": null,
                "google_ts": null,
                "provider": "anthropic"
            },
            "token_usage": null,
            "timestamp_ms": 1772197680810
        }
        """.trimIndent()

        val actual = json.decodeFromString<ResponseNode>(responseNodeJson)

        val expected = ResponseNode(
            id = 0,
            type = 8,
            content = "",
            toolUse = null,
            thinking = Thinking(
                summary = "The user wants me to see a TODO file.",
                encryptedContent = "EsECCkgICxACGAIqQKwV9Ej8245MvW4...",
                content = null,
                openaiResponsesApiItemId = null
            ),
            billingMetadata = null,
            metadata = Metadata(openaiId = null, googleTs = null, provider = "anthropic", phase = null),
            tokenUsage = null,
            timestampMs = 1772197680810
        )

        assertEquals(expected, actual)
    }

    @Test
    fun shouldDeserializeThinkingWithDecryptedContent() {
        val thinkingJson = """
        {
            "summary": "Analyzing the code",
            "encrypted_content": "encrypted...",
            "content": "This is the decrypted thinking content",
            "openai_responses_api_item_id": "resp_abc123"
        }
        """.trimIndent()

        val actual = json.decodeFromString<Thinking>(thinkingJson)

        val expected = Thinking(
            summary = "Analyzing the code",
            encryptedContent = "encrypted...",
            content = "This is the decrypted thinking content",
            openaiResponsesApiItemId = "resp_abc123"
        )

        assertEquals(expected, actual)
    }

    @Test
    fun shouldDeserializeResponseNodeType10TokenUsage() {
        val responseNodeJson = """
        {
            "id": 1,
            "type": 10,
            "content": "",
            "tool_use": null,
            "thinking": null,
            "billing_metadata": null,
            "metadata": null,
            "token_usage": {
                "input_tokens": 9,
                "output_tokens": 171,
                "cache_read_input_tokens": 9986,
                "cache_creation_input_tokens": 549,
                "system_prompt_tokens": 4087,
                "chat_history_tokens": 0,
                "current_message_tokens": 20,
                "max_context_tokens": 204800,
                "tool_definitions_tokens": 7387,
                "tool_result_tokens": 0,
                "assistant_response_tokens": 171
            },
            "timestamp_ms": 1772197681579
        }
        """.trimIndent()

        val actual = json.decodeFromString<ResponseNode>(responseNodeJson)

        val expected = ResponseNode(
            id = 1,
            type = 10,
            content = "",
            toolUse = null,
            thinking = null,
            billingMetadata = null,
            metadata = null,
            tokenUsage = TokenUsage(
                inputTokens = 9,
                outputTokens = 171,
                cacheReadInputTokens = 9986,
                cacheCreationInputTokens = 549,
                systemPromptTokens = 4087,
                chatHistoryTokens = 0,
                currentMessageTokens = 20,
                maxContextTokens = 204800,
                toolDefinitionsTokens = 7387,
                toolResultTokens = 0,
                assistantResponseTokens = 171
            ),
            timestampMs = 1772197681579
        )

        assertEquals(expected, actual)
    }

    @Test
    fun shouldDeserializeTokenUsageWithOptionalFieldsMissing() {
        val tokenUsageJson = """
        {
            "input_tokens": 10,
            "output_tokens": 50,
            "cache_read_input_tokens": 1000,
            "cache_creation_input_tokens": 100,
            "system_prompt_tokens": 500,
            "chat_history_tokens": 200,
            "current_message_tokens": 30,
            "max_context_tokens": 100000
        }
        """.trimIndent()

        val actual = json.decodeFromString<TokenUsage>(tokenUsageJson)

        val expected = TokenUsage(
            inputTokens = 10,
            outputTokens = 50,
            cacheReadInputTokens = 1000,
            cacheCreationInputTokens = 100,
            systemPromptTokens = 500,
            chatHistoryTokens = 200,
            currentMessageTokens = 30,
            maxContextTokens = 100000,
            toolDefinitionsTokens = null,
            toolResultTokens = null,
            assistantResponseTokens = null
        )

        assertEquals(expected, actual)
    }

    @Test
    fun shouldDeserializeMetadataWithAllFields() {
        val metadataJson = """
        {
            "openai_id": "chatcmpl-abc123",
            "google_ts": "2026-01-16T12:00:00Z",
            "provider": "anthropic",
            "phase": "generation"
        }
        """.trimIndent()

        val actual = json.decodeFromString<Metadata>(metadataJson)

        val expected = Metadata(
            openaiId = "chatcmpl-abc123",
            googleTs = "2026-01-16T12:00:00Z",
            provider = "anthropic",
            phase = "generation"
        )

        assertEquals(expected, actual)
    }

    @Test
    fun shouldDeserializeMetadataWithAllNullFields() {
        val metadataJson = """
        {
            "openai_id": null,
            "google_ts": null,
            "provider": null,
            "phase": null
        }
        """.trimIndent()

        val actual = json.decodeFromString<Metadata>(metadataJson)

        val expected = Metadata(
            openaiId = null,
            googleTs = null,
            provider = null,
            phase = null
        )

        assertEquals(expected, actual)
    }

    @Test
    fun shouldDeserializeFullSessionWithAllNodeTypes() {
        val fullSessionJson = """
        {
            "sessionId": "full-test-session",
            "created": "2026-01-16T12:00:00.000Z",
            "modified": "2026-01-16T12:30:00.000Z",
            "chatHistory": [
                {
                    "exchange": {
                        "request_message": "See TODO",
                        "response_text": "Here is the TODO file content",
                        "request_id": "req-001",
                        "request_nodes": [
                            {
                                "id": 1,
                                "type": 0,
                                "text_node": { "content": "See TODO" }
                            },
                            {
                                "id": 2,
                                "type": 4,
                                "ide_state_node": {
                                    "workspace_folders": [
                                        { "repository_root": "d:\\project", "folder_root": "d:\\project" }
                                    ],
                                    "workspace_folders_unchanged": false,
                                    "current_terminal": { "terminal_id": 0, "current_working_directory": "d:\\project" }
                                }
                            }
                        ],
                        "response_nodes": [
                            {
                                "id": 0,
                                "type": 8,
                                "content": "",
                                "thinking": {
                                    "summary": "Looking for TODO file",
                                    "encrypted_content": "encrypted..."
                                },
                                "metadata": { "provider": "anthropic" }
                            },
                            {
                                "id": 1,
                                "type": 5,
                                "content": "",
                                "tool_use": {
                                    "tool_use_id": "toolu_001",
                                    "tool_name": "view",
                                    "input_json": "{\"path\": \"TODO.txt\"}",
                                    "is_partial": false
                                }
                            },
                            {
                                "id": 2,
                                "type": 10,
                                "content": "",
                                "token_usage": {
                                    "input_tokens": 100,
                                    "output_tokens": 200,
                                    "cache_read_input_tokens": 5000,
                                    "cache_creation_input_tokens": 500,
                                    "system_prompt_tokens": 4000,
                                    "chat_history_tokens": 100,
                                    "current_message_tokens": 50,
                                    "max_context_tokens": 200000
                                }
                            },
                            {
                                "id": 3,
                                "type": 0,
                                "content": "Here is the TODO file content",
                                "metadata": { "provider": "anthropic" }
                            }
                        ]
                    },
                    "completed": true,
                    "sequenceId": 1,
                    "finishedAt": "2026-01-16T12:05:00.000Z",
                    "changedFiles": [],
                    "changedFilesSkipped": [],
                    "changedFilesSkippedCount": 0,
                    "source": "local"
                }
            ],
            "agentState": {
                "userGuidelines": "Be helpful",
                "workspaceGuidelines": "",
                "agentMemories": "",
                "modelId": "claude-opus-4-5",
                "userEmail": "test@example.com"
            },
            "rootTaskUuid": "root-task-uuid",
            "customTitle": "TODO File Review"
        }
        """.trimIndent()

        val actual = json.decodeFromString<Session>(fullSessionJson)

        val expected = Session(
            sessionId = "full-test-session",
            created = "2026-01-16T12:00:00.000Z",
            modified = "2026-01-16T12:30:00.000Z",
            chatHistory = listOf(
                ChatHistory(
                    exchange = Exchange(
                        requestMessage = "See TODO",
                        responseText = "Here is the TODO file content",
                        requestId = "req-001",
                        requestNodes = listOf(
                            RequestNode(
                                id = 1, type = 0,
                                textNode = TextNode(content = "See TODO"),
                                toolResultNode = null, ideStateNode = null
                            ),
                            RequestNode(
                                id = 2, type = 4,
                                textNode = null, toolResultNode = null,
                                ideStateNode = IdeStateNode(
                                    workspaceFolders = listOf(
                                        WorkspaceFolder(repositoryRoot = "d:\\project", folderRoot = "d:\\project")
                                    ),
                                    workspaceFoldersUnchanged = false,
                                    currentTerminal = CurrentTerminal(terminalId = 0, currentWorkingDirectory = "d:\\project")
                                )
                            )
                        ),
                        responseNodes = listOf(
                            ResponseNode(
                                id = 0, type = 8, content = "",
                                toolUse = null,
                                thinking = Thinking(summary = "Looking for TODO file", encryptedContent = "encrypted..."),
                                billingMetadata = null,
                                metadata = Metadata(provider = "anthropic"),
                                tokenUsage = null, timestampMs = null
                            ),
                            ResponseNode(
                                id = 1, type = 5, content = "",
                                toolUse = ToolUse(
                                    toolUseId = "toolu_001", toolName = "view",
                                    inputJson = """{"path": "TODO.txt"}""", isPartial = false
                                ),
                                thinking = null, billingMetadata = null, metadata = null, tokenUsage = null, timestampMs = null
                            ),
                            ResponseNode(
                                id = 2, type = 10, content = "",
                                toolUse = null, thinking = null, billingMetadata = null, metadata = null,
                                tokenUsage = TokenUsage(
                                    inputTokens = 100, outputTokens = 200,
                                    cacheReadInputTokens = 5000, cacheCreationInputTokens = 500,
                                    systemPromptTokens = 4000, chatHistoryTokens = 100,
                                    currentMessageTokens = 50, maxContextTokens = 200000
                                ),
                                timestampMs = null
                            ),
                            ResponseNode(
                                id = 3, type = 0, content = "Here is the TODO file content",
                                toolUse = null, thinking = null, billingMetadata = null,
                                metadata = Metadata(provider = "anthropic"),
                                tokenUsage = null, timestampMs = null
                            )
                        )
                    ),
                    completed = true,
                    sequenceId = 1.0,
                    finishedAt = "2026-01-16T12:05:00.000Z",
                    changedFiles = emptyList(),
                    changedFilesSkipped = emptyList(),
                    changedFilesSkippedCount = 0,
                    isHistorySummary = null,
                    historySummaryVersion = null,
                    source = "local"
                )
            ),
            agentState = AgentState(
                userGuidelines = "Be helpful",
                workspaceGuidelines = "",
                agentMemories = "",
                modelId = "claude-opus-4-5",
                userEmail = "test@example.com"
            ),
            rootTaskUuid = "root-task-uuid",
            customTitle = "TODO File Review",
            terminalId = null
        )

        assertEquals(expected, actual)
    }
}
