package antifraud.controller;

import antifraud.constraints.IPv4Format;
import antifraud.constraints.Luhn;
import antifraud.entity.StolenCard;
import antifraud.entity.SuspiciousIP;
import antifraud.entity.Transaction;
import antifraud.entity.request.TransactionFeedback;
import antifraud.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/antifraud")
@Validated
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transaction")
    public ResponseEntity<?> validateTransaction(@Valid @RequestBody Transaction transaction) {
        return transactionService.validateTransaction(transaction);
    }

    @PostMapping("/suspicious-ip")
    public ResponseEntity<?> addSuspiciousIp(@Valid @RequestBody SuspiciousIP ip) {
        return transactionService.addSuspiciousIp(ip);
    }

    @DeleteMapping("/suspicious-ip/{ip}")
    public ResponseEntity<?> deleteSuspiciousIp(@Valid @PathVariable @IPv4Format String ip) {
       return transactionService.deleteSuspiciousIp(ip);
    }

    @GetMapping("/suspicious-ip")
    public ResponseEntity<List<SuspiciousIP>> getAllSuspiciousIp() {
        return transactionService.getAllSuspiciousIp();
    }

    @PostMapping("/stolencard")
    public ResponseEntity<?> addStolenCard(@Valid @RequestBody StolenCard card) {
        return transactionService.addStolenCard(card);
    }

    @GetMapping("/stolencard")
    public ResponseEntity<List<StolenCard>> getAllStolenCards() {
        return transactionService.getAllStolenCards();
    }

    @DeleteMapping("/stolencard/{number}")
    public ResponseEntity<?> deleteStolenCard(@Valid @PathVariable @Luhn String number) {
        return transactionService.deleteStolenCard(number);
    }

    @GetMapping("/history")
    public ResponseEntity<List<Transaction>> getHistory() {
        return transactionService.getHistory();
    }

    @GetMapping("/history/{number}")
    public ResponseEntity<List<Transaction>> getHistoryByCardNumber(@Valid @PathVariable @Luhn String number) {
        return transactionService.getHistoryByCardNumber(number);
    }

    @PutMapping("/transaction")
    public ResponseEntity<?> updateTransaction(@Valid @RequestBody TransactionFeedback feedback) {
        return transactionService.addFeedback(feedback);
    }

}