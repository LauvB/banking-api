package com.laurabeltran.banking_api.infrastructure.web.transfer;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laurabeltran.banking_api.application.transfer.TransferMoneyUseCase;
import com.laurabeltran.banking_api.application.transfer.TransferResult;
import com.laurabeltran.banking_api.application.transfer.dto.TransferMoneyRequest;
import com.laurabeltran.banking_api.application.transfer.dto.TransferResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/transfers")
public class TransferController {

    private final TransferMoneyUseCase transferMoneyUseCase;

    public TransferController(TransferMoneyUseCase transferMoneyUseCase) {
        this.transferMoneyUseCase = transferMoneyUseCase;
    }

    @PostMapping
    public TransferResponse transfer(
            @Valid @RequestBody TransferMoneyRequest request) {

        TransferResult result = transferMoneyUseCase.execute(
                request.sourceAccountId(),
                request.targetAccountId(),
                request.amount());

        return new TransferResponse(
                result.sourceAccountId(),
                result.targetAccountId(),
                result.amount(),
                result.sourceBalance(),
                result.targetBalance());

    }
}
