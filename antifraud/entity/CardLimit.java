package antifraud.entity;

import antifraud.constraints.Luhn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "card_limit")
public class CardLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Luhn
    private String number;

    private long allowedAmountLimit;
    private long manualAmountLimit;

}