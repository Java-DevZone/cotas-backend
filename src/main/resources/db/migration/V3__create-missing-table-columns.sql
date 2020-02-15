CREATE TABLE wallet_history (
    id BIGINT NOT NULL AUTO_INCREMENT,
    wallet_id MEDIUMINT NOT NULL,
    quota DECIMAL(14,7),
    total_quotas DECIMAL(14,7),
    register_date DATE,
    PRIMARY KEY (id)
);

ALTER TABLE asset_history ADD COLUMN (
    quantity DECIMAL(14,7)
);

ALTER TABLE investment DROP COLUMN  quota_holder_id;
