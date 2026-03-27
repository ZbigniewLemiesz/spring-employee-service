CREATE TABLE IF NOT EXISTS password_setup_tokens (
    id bigint primary key auto_increment,
    user_account_id bigint not null,
    token_hash varchar(128) not null,
    expires_at timestamp not null,
    used_at timestamp null,
    created_at timestamp not null default current_timestamp,

    constraint fk_password_setup_tokens_user_account
        foreign key (user_account_id)
        references user_accounts (id)
        on delete cascade
);

CREATE UNIQUE INDEX  uk_password_setup_tokens_token_hash
    on password_setup_tokens (token_hash);

CREATE INDEX idx_password_setup_token_user_account_id
ON password_setup_tokens (user_account_id);

CREATE INDEX idx_password_setup_token_expires_at
ON password_setup_tokens (expires_at);