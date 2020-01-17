CREATE TABLE wallet (
    id MEDIUMINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    quota DECIMAL(14,7),
    total_value DECIMAL(14,7),
    quota_updated_at TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE asset (
    ticket VARCHAR(100) NOT NULL,
    type VARCHAR(30),
    PRIMARY KEY (ticket)
);

CREATE TABLE asset_history (
    id MEDIUMINT NOT NULL AUTO_INCREMENT,
    value DECIMAL(14,7),
    dateTime TIMESTAMP,
    asset_ticket VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (asset_ticket)
        REFERENCES asset(ticket)
);

CREATE TABLE investment (
    id MEDIUMINT NOT NULL AUTO_INCREMENT,
    value DECIMAL(14,7),
    quantity INTEGER,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    asset_ticket VARCHAR(100),
    wallet_id MEDIUMINT,
    quota_holder_id MEDIUMINT,
    PRIMARY KEY (id),
    FOREIGN KEY (asset_ticket)
        REFERENCES asset(ticket),
    FOREIGN KEY (wallet_id)
        REFERENCES wallet(id)
);

CREATE TABLE wallet_investments (
    wallet_id MEDIUMINT NOT NULL,
    investment_id MEDIUMINT NOT NULL,
    PRIMARY KEY (wallet_id, investment_id),
    FOREIGN KEY (wallet_id)
            REFERENCES wallet(id),
    FOREIGN KEY (investment_id)
            REFERENCES investment(id)
);

CREATE TABLE quota_holder (
    id MEDIUMINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    opt_in_at TIMESTAMP,
    opt_out_at TIMESTAMP,
    wallet_id MEDIUMINT,
    PRIMARY KEY (id)
);

CREATE TABLE wallet_quota_holders (
    wallet_id MEDIUMINT NOT NULL,
    quota_holder_id MEDIUMINT NOT NULL,
    PRIMARY KEY (wallet_id, quota_holder_id),
    FOREIGN KEY (wallet_id)
        REFERENCES wallet(id),
    FOREIGN KEY (quota_holder_id)
        REFERENCES quota_holder(id)
);

