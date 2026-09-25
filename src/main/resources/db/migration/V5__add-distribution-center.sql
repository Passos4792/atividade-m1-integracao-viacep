-- Mantém V1 a V4 intactas. Distribuição manual dos 20 produtos da V4.
ALTER TABLE product ADD distribution_center TEXT;
UPDATE product SET distribution_center = 'Mogi das Cruzes' WHERE id IN (
    'dfc70678-865e-4505-9451-8ae8f49d5d61',
    '795e935c-d12d-4666-b748-d2c91656ac14',
    '4e6100d7-eee8-416f-ab4e-cc2d4086180c',
    'bca94e15-347b-4006-b38d-239c564b563f',
    'c0659313-c232-4308-b87e-965209ee95ce',
    'bf8580dc-e8b2-47eb-a5ff-ceadb0fdd248',
    '7bb07e18-b60f-476d-bc64-54bb7d454885'
);

UPDATE product SET distribution_center = 'Recife' WHERE id IN (
    'bd52ca2c-311b-4354-b2ea-cb5e46e3cc62',
    '703ea512-9a11-4c24-ad0a-49afd304f98b',
    '5c7f4f0a-b1b5-441c-8b71-684b6eb5118f',
    '56f8aed2-b76f-4619-9120-23e826db990a',
    '53690a36-aeb4-4df0-9d27-a9820253041c',
    'ce587941-d226-4ec3-8271-ea26944a5ec2'
);

UPDATE product SET distribution_center = 'Porto Alegre' WHERE id IN (
    'a95f0b57-4a69-495e-9532-e98874b9fe2f',
    '71c03a5b-73e5-43fc-8c60-bb1eeb7a3828',
    'a802ea0e-08d1-43f0-99e6-de3bd00ff82c',
    '121969ae-822b-4995-a91c-d6f03cca74c6',
    '907eef26-5fe6-4d44-b1a8-9777d2dfa8d7',
    '0798813a-fd46-4f45-afdc-8100297e1350',
    'be3c9f74-eb42-403e-bd9d-1cf5bd6d6f67'
);
-- Também cobre produtos criados pelo usuário antes desta migration.
UPDATE product SET distribution_center = 'Mogi das Cruzes' WHERE distribution_center IS NULL;
ALTER TABLE product ALTER COLUMN distribution_center SET NOT NULL;
ALTER TABLE product ADD CONSTRAINT product_distribution_center_check
    CHECK (distribution_center IN ('Mogi das Cruzes', 'Recife', 'Porto Alegre'));
