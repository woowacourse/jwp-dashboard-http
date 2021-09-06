package nextstep.jwp.model;

import java.util.Objects;
import nextstep.jwp.exception.BadRequestException;

public class User {

    private long id;
    private final String account;
    private final String password;
    private final String email;

    public User(String account, String password) {
        this(0L, account, password, "");
    }

    public User(long id, String account, String password, String email) {
        validate(account, password);
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    private void validate(String account, String password) {
        if (Objects.isNull(account) || Objects.isNull(password)
            || account.length() < 1 || password.length() < 1) {
            throw new BadRequestException();
        }
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public long getId() {
        return id;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }
}
