package antifraud.service;

import antifraud.entity.CardLimit;
import antifraud.entity.StolenCard;
import antifraud.entity.SuspiciousIP;
import antifraud.entity.Transaction;
import antifraud.entity.request.TransactionFeedback;
import antifraud.entity.response.Result;
import antifraud.entity.response.Status;
import antifraud.repository.CardLimitRepository;
import antifraud.repository.StolenCardRepository;
import antifraud.repository.SuspiciousIPRepository;
import antifraud.repository.TransactionRepository;
import antifraud.util.Feedback;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final String ALLOWED = Feedback.ALLOWED.toString();
    private final String MANUAL_PROCESSING = Feedback.MANUAL_PROCESSING.toString();
    private final String PROHIBITED = Feedback.PROHIBITED.toString();

    private final SuspiciousIPRepository suspiciousIPRepository;
    private final StolenCardRepository stolenCardRepository;
    private final TransactionRepository transactionRepository;
    private final CardLimitRepository cardLimitRepository;

    public TransactionService(SuspiciousIPRepository suspiciousIPRepository,
                              StolenCardRepository stolenCardRepository,
                              TransactionRepository transactionRepository,
                              CardLimitRepository cardLimitRepository) {
        this.suspiciousIPRepository = suspiciousIPRepository;
        this.stolenCardRepository = stolenCardRepository;
        this.transactionRepository = transactionRepository;
        this.cardLimitRepository = cardLimitRepository;
    }

    @Transactional
    public ResponseEntity<Result> validateTransaction(Transaction transaction) {
        long amount = transaction.getAmount();
        String number = transaction.getNumber();
        String ip = transaction.getIp();
        String region = transaction.getRegion();
        LocalDateTime date = transaction.getDate();

        createCardLimitIfNotExists(number);
        CardLimit card = cardLimitRepository.findByNumber(number).get();

        long limitAllowed = card.getAllowedAmountLimit();
        long limitManual = card.getManualAmountLimit();

        int ipCounter = 0;
        int regionCounter = 0;

        if (amount <= 0) {
            return new ResponseEntity<>(new Result("Wrong request!", "amount"), HttpStatus.BAD_REQUEST);
        }

        List<Transaction> transactions = getLastHourTransactions(number, date);
        for (Transaction t : transactions) {
            if (!t.getIp().equals(ip)) {
                ipCounter++;
            }
            if (!t.getRegion().equals(region)) {
                regionCounter++;
            }
        }

        if (isCardNumberBlacklisted(number) && isIpBlacklisted(ip) && amount > limitManual && (ipCounter > 3 || regionCounter > 3)) {
            String info = ipCounter > 3 && regionCounter > 3 ? "amount, card-number, ip, ip-correlation, region-correlation" :
                    ipCounter > 3 ? "amount, card-number, ip, ip-correlation" : "amount, card-number, ip, region-correlation";
            transaction.setResult(PROHIBITED);
            transactionRepository.save(transaction);
            return new ResponseEntity<>(new Result(PROHIBITED, info), HttpStatus.OK);
        } else if (isCardNumberBlacklisted(number) && isIpBlacklisted(ip) && amount > limitManual) {
            transaction.setResult(PROHIBITED);
            transactionRepository.save(transaction);
            return new ResponseEntity<>(new Result(PROHIBITED, "amount, card-number, ip"), HttpStatus.OK);
        } else if (isCardNumberBlacklisted(number) || isIpBlacklisted(ip)) {
            String info = isCardNumberBlacklisted(number) && isIpBlacklisted(ip) ? "card-number, ip" :
                    isCardNumberBlacklisted(number) ? "card-number" : "ip";
            transaction.setResult(PROHIBITED);
            transactionRepository.save(transaction);
            return new ResponseEntity<>(new Result("PROHIBITED", info), HttpStatus.OK);
        } else if (amount > limitManual) {
            transaction.setResult(PROHIBITED);
            transactionRepository.save(transaction);
            return new ResponseEntity<>(new Result(PROHIBITED, "amount"), HttpStatus.OK);
        } else if (ipCounter == 3 || regionCounter == 3) {
            transaction.setResult(MANUAL_PROCESSING);
            transactionRepository.save(transaction);
            String info = ipCounter == 3 && regionCounter == 3 ? "ip-correlation, region-correlation" :
                    ipCounter == 3 ? "ip-correlation" : "region-correlation";
            return new ResponseEntity<>(new Result(MANUAL_PROCESSING, info), HttpStatus.OK);
        } else if (ipCounter > 3 || regionCounter > 3) {
            transaction.setResult(PROHIBITED);
            transactionRepository.save(transaction);
            String info = ipCounter > 3 && regionCounter > 3 ? "ip-correlation, region-correlation" :
                    ipCounter > 3 ? "ip-correlation" : "region-correlation";
            return new ResponseEntity<>(new Result(PROHIBITED, info), HttpStatus.OK);
        } else if (amount > limitAllowed) {
            transaction.setResult(MANUAL_PROCESSING);
            transactionRepository.save(transaction);
            return new ResponseEntity<>(new Result(MANUAL_PROCESSING, "amount"), HttpStatus.OK);
        }
        transaction.setResult(ALLOWED);
        transactionRepository.save(transaction);
        return new ResponseEntity<>(new Result(ALLOWED, "none"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> addSuspiciousIp(SuspiciousIP ip) {
        if (suspiciousIPRepository.existsByIp(ip.getIp())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(suspiciousIPRepository.save(ip), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> deleteSuspiciousIp(String ip) {
        Optional<SuspiciousIP> suspiciousIP = suspiciousIPRepository.findByIp(ip);
        if (suspiciousIP.isPresent()) {
            suspiciousIPRepository.delete(suspiciousIP.get());
            return new ResponseEntity<>(new Status("IP " + ip + " successfully removed!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Status("IP " + ip + " not found"), HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<SuspiciousIP>> getAllSuspiciousIp() {
        return new ResponseEntity<>(suspiciousIPRepository.findAll(), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> addStolenCard(StolenCard card) {
        Optional<StolenCard> stolenCard = stolenCardRepository.findByNumber(card.getNumber());
        if (stolenCard.isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(stolenCardRepository.save(card), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> deleteStolenCard(String number) {
        Optional<StolenCard> stolenCard = stolenCardRepository.findByNumber(number);
        if (stolenCard.isPresent()) {
            stolenCardRepository.delete(stolenCard.get());
            return new ResponseEntity<>(new Status("Card " + number + " successfully removed!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Status("Card " + number + " not found"), HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<StolenCard>> getAllStolenCards() {
        return new ResponseEntity<>(stolenCardRepository.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<List<Transaction>> getHistory() {
        return new ResponseEntity<>(transactionRepository.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<List<Transaction>> getHistoryByCardNumber(String number) {
        List<Transaction> transactions = transactionRepository.findAllByNumber(number);
        if (transactions.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> addFeedback(TransactionFeedback feedbackResponse) {
        Optional<Transaction> transaction = transactionRepository.findById(feedbackResponse.getTransactionId());
        if (transaction.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        String feedback = feedbackResponse.getFeedback();
        String result = transaction.get().getResult();
        String number = transaction.get().getNumber();

        if (!transaction.get().getFeedback().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } else if (result.equals(feedback)) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        CardLimit card = cardLimitRepository.findByNumber(number).get();

        transaction.get().setFeedback(feedbackResponse.getFeedback());
        transactionRepository.save(transaction.get());

        changeLimit(transaction.get(), card);
        cardLimitRepository.save(card);

        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    private List<Transaction> getLastHourTransactions(String number, LocalDateTime date) {
        LocalDateTime lastHour = date.minusHours(1);
        return transactionRepository.findAllByNumberAndDateBetween(number, lastHour, date);
    }

    private boolean isIpBlacklisted(String ip) {
        Optional<SuspiciousIP> suspiciousIP = suspiciousIPRepository.findByIp(ip);
        return suspiciousIP.isPresent();
    }

    private boolean isCardNumberBlacklisted(String number) {
        Optional<StolenCard> stolenCard = stolenCardRepository.findByNumber(number);
        return stolenCard.isPresent();
    }

    private void changeLimit(Transaction transaction, CardLimit card) {
        String feedback = transaction.getFeedback();
        String result = transaction.getResult();
        long amount = transaction.getAmount();

        if (feedback.equals(ALLOWED)) {
            if (result.equals(MANUAL_PROCESSING)) {
                card.setAllowedAmountLimit(increase(card.getAllowedAmountLimit(), amount));
            } else if (result.equals(PROHIBITED)) {
                card.setAllowedAmountLimit(increase(card.getAllowedAmountLimit(), amount));
                card.setManualAmountLimit(increase(card.getManualAmountLimit(), amount));
            }
        } else if (transaction.getFeedback().equals(MANUAL_PROCESSING)) {
            if (result.equals(ALLOWED)) {
                card.setAllowedAmountLimit(decrease(card.getAllowedAmountLimit(), amount));
            } else if (result.equals(PROHIBITED)) {
                card.setManualAmountLimit(increase(card.getManualAmountLimit(), amount));
            }
        } else if (transaction.getFeedback().equals(PROHIBITED)) {
            if (result.equals(ALLOWED)) {
                card.setAllowedAmountLimit(decrease(card.getAllowedAmountLimit(), amount));
                card.setManualAmountLimit(decrease(card.getManualAmountLimit(), amount));
            } else if (result.equals(MANUAL_PROCESSING)) {
                card.setManualAmountLimit(decrease(card.getManualAmountLimit(), amount));
            }
        }
    }

    private long increase(long currentLimit, long amountFromTransaction) {
        return (long) Math.ceil(0.8 * currentLimit + 0.2 * amountFromTransaction);
    }

    private long decrease(long currentLimit, long amountFromTransaction) {
        return (long) Math.ceil(0.8 * currentLimit - 0.2 * amountFromTransaction);
    }

    private void createCardLimitIfNotExists(String number) {
        if (cardLimitRepository.findByNumber(number).isEmpty()) {
            CardLimit cardLimit = new CardLimit();
            cardLimit.setNumber(number);
            cardLimit.setAllowedAmountLimit(200);
            cardLimit.setManualAmountLimit(1500);
            cardLimitRepository.save(cardLimit);
        }
    }
}