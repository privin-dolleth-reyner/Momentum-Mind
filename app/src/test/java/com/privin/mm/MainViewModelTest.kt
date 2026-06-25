package com.privin.mm

import com.privin.data.QuotesRepository
import com.privin.data.models.Quote
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: QuotesRepository = mockk(relaxed = true)

    private fun viewModel() = MainViewModel(repository, mainDispatcherRule.testDispatcher)

    @Test
    fun `init exposes Loaded state when repository emits a non-empty quote`() = runTest {
        val quote = Quote(date = "25-6-2026", quote = "Rise up", author = "Sun")
        coEvery { repository.getDailyQuote() } returns flowOf(quote)

        val vm = viewModel()

        val state = vm.state.value
        assertTrue(state is MainState.Loaded)
        assertEquals(quote, (state as MainState.Loaded).quote)
    }

    @Test
    fun `init ignores an empty quote and stays in Loading`() = runTest {
        coEvery { repository.getDailyQuote() } returns flowOf(Quote())

        val vm = viewModel()

        assertTrue(vm.state.value is MainState.Loading)
    }

    @Test
    fun `init surfaces Error state when the repository throws`() = runTest {
        coEvery { repository.getDailyQuote() } throws RuntimeException("network down")

        val vm = viewModel()

        assertTrue(vm.state.value is MainState.Error)
    }

    @Test
    fun `addQuoteToFavorites delegates the quote to the repository unchanged`() = runTest {
        coEvery { repository.getDailyQuote() } returns flowOf(Quote())
        coEvery { repository.updateFavorites(any()) } returns Unit
        val vm = viewModel()

        val favourite = Quote(date = "10-6-2026", quote = "Keep", author = "Me", isFavorite = true)
        vm.addQuoteToFavorites(favourite)

        coVerify(exactly = 1) { repository.updateFavorites(favourite) }
    }
}
