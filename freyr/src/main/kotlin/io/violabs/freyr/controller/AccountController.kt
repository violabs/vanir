package io.violabs.freyr.controller

import io.violabs.freyr.domain.Account
import io.violabs.freyr.service.AccountService
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/accounts")
class AccountController(private val accountService: AccountService) {

    @GetMapping
    fun getAllAccounts(): Flow<Account> = accountService.listAccounts()
}