CREATE TABLE wallet_history (
    wallet_id MEDIUMINT NOT NULL AUTO_INCREMENT,
    quota DECIMAL(14,7),
    total_quotas DECIMAL(14,7),
    register_date DATE,
    PRIMARY KEY (wallet_id)
);

ALTER TABLE asset_history ADD COLUMN (
    quantity DECIMAL(14,7)
);

ALTER TABLE investment DROP COLUMN  quota_holder_id;
