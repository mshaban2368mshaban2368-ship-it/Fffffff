package com.example

import com.example.data.ChatRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleRobolectricTest {

  @Test
  fun `verify FA97 code and backup key format`() {
    val repo = ChatRepository()
    val code = repo.generateNewFa97Code()
    val key = repo.generateBackupKey()

    assertTrue("Code should start with FA97-", code.startsWith("FA97-"))
    assertEquals(11, code.length)
    assertTrue("Key should start with FA97-SEC-", key.startsWith("FA97-SEC-"))
  }

  @Test
  fun `verify initial contacts and chats seeded`() {
    val repo = ChatRepository()
    assertTrue(repo.contacts.value.isNotEmpty())
    assertTrue(repo.chats.value.isNotEmpty())
  }
}
