INSERT IGNORE INTO
    model_pricing
    (name, price)
VALUES
    # Values are for 1k tokens.
    ('text-davinci-003', 0.02),
    ('text-curie-001', 0.002),
    ('text-babbage-001', 0.0005),
    ('text-ada-001', 0.0004);
