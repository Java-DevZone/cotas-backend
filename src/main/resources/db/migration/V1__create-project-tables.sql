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
    quantity INTEGER,
    PRIMARY KEY (ticket)
);

CREATE TABLE wallet_assets (
    wallet_id MEDIUMINT NOT NULL,
    asset_id MEDIUMINT NOT NULL
);

CREATE TABLE quota_holder (
    id MEDIUMINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    opt_in_at TIMESTAMP,
    opt_out_at TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE wallet_quota_holders (
    wallet_id MEDIUMINT NOT NULL,
    quota_holder_id MEDIUMINT NOT NULL
);

CREATE TABLE investment (
    id MEDIUMINT NOT NULL AUTO_INCREMENT,
    value DECIMAL(14,7),
    date_time TIMESTAMP,
    quota_holder_id MEDIUMINT,
    PRIMARY KEY (id)
);
