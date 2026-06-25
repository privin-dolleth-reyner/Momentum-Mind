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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavouritesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: QuotesRepository = mockk(relaxed = true)

    private fun viewModel() = FavouritesViewModel(repository, mainDispatcherRule.testDispatcher)

    @Test
    fun `init loads favourites into the Loaded state`() = runTest {
        val favourites = listOf(
            Quote(date = "1-1-2026", quote = "A", author = "x", isFavorite = true),
            Quote(date = "2-1-2026", quote = "B", author = "y", isFavorite = true)
        )
        coEvery { repository.getFavourites() } returns flowOf(favourites)

        val vm = viewModel()

        val uiState = vm.state.value.uiState
        assertTrue(uiState is FavouritesState.Loaded)
        assertEquals(favourites, (uiState as FavouritesState.Loaded).quotes)
    }

    @Test
    fun `init falls back to an empty Loaded state when loading throws`() = runTest {
        coEvery { repository.getFavourites() } throws RuntimeException("db error")

        val vm = viewModel()

        val uiState = vm.state.value.uiState
        assertTrue(uiState is FavouritesState.Loaded)
        assertTrue((uiState as FavouritesState.Loaded).quotes.isEmpty())
    }

    @Test
    fun `setSelectedQuote and resetSelectedQuote update the selected quote`() = runTest {
        coEvery { repository.getFavourites() } returns flowOf(emptyList())
        val vm = viewModel()
        val quote = Quote(date = "3-1-2026", quote = "Chosen", author = "z", isFavorite = true)

        vm.setSelectedQuote(quote)
        assertEquals(quote, vm.state.value.selectedQuote)

        vm.resetSelectedQuote()
        assertNull(vm.state.value.selectedQuote)
    }

    @Test
    fun `updateQuoteFavorites applies the favourite flag before delegating to the repository`() = runTest {
        coEvery { repository.getFavourites() } returns flowOf(emptyList())
        coEvery { repository.updateFavorites(any()) } returns Unit
        val vm = viewModel()

        val quote = Quote(date = "10-6-2026", quote = "Toggle me", author = "a", isFavorite = true)
        vm.updateQuoteFavorites(quote, isFavorite = false)

        // The persisted quote must carry the new flag while keeping its original date.
        coVerify(exactly = 1) { repository.updateFavorites(quote.copy(isFavorite = false)) }
    }
}
