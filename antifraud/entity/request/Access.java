package antifraud.entity.request;

import antifraud.constraints.AccessValid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class Access {

    @NotBlank
    private String username;

    @AccessValid
    private String operation;
}