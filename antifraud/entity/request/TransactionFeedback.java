package antifraud.entity.request;

import antifraud.constraints.FeedbackValid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class TransactionFeedback {

        @NotNull
        private Long transactionId;

        @FeedbackValid
        private String feedback;
}