package com.wedj.tv

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    authUseCase: AuthUseCase
) : ReduxViewModel<MainViewState>(MainViewState()) {

    init {
        viewModelScope.launch {
            when (authUseCase.checkUserStatus()) {

                AuthUseCase.UseCaseResult.ShowHome -> {
                    setState { copy(mainNavigation = Home) }
                }
                AuthUseCase.UseCaseResult.ShowLogin -> {
                    setState { copy(mainNavigation = Login) }
                }
                AuthUseCase.UseCaseResult.ShowForgotPassword -> {
                    setState { copy(mainNavigation = ForgotPassword) }
                }
                AuthUseCase.UseCaseResult.ShowDashboard -> {
                    setState { copy(mainNavigation = Dashboard) }
                }
            }
        }
    }
}