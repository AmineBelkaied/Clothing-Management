-- phpMyAdmin SQL Dump
-- version 5.0.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : mar. 26 déc. 2023 à 20:09
-- Version du serveur :  10.4.11-MariaDB
-- Version de PHP : 7.4.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `clothing-management-tenant`
--
CREATE DATABASE IF NOT EXISTS `clothing-management-tenant` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `clothing-management-tenant`;
-- --------------------------------------------------------

--
-- Structure de la table `city`
--

CREATE TABLE `city` (
                        `id` bigint(20) NOT NULL,
                        `name` varchar(255) DEFAULT NULL,
                        `postal_code` varchar(255) DEFAULT NULL,
                        `governorate_id` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `color`
--

CREATE TABLE `color` (
                         `id` bigint(20) NOT NULL,
                         `name` varchar(255) DEFAULT NULL,
                         `reference` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `fb_page`
--

CREATE TABLE `fb_page` (
                           `id` bigint(20) NOT NULL,
                           `enabled` bit(1) NOT NULL,
                           `link` varchar(255) DEFAULT NULL,
                           `name` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `governorate`
--

CREATE TABLE `governorate` (
                               `id` bigint(20) NOT NULL,
                               `delivery_id` int(11) NOT NULL,
                               `name` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `model`
--

CREATE TABLE `model` (
                         `id` bigint(20) NOT NULL,
                         `description` varchar(255) DEFAULT NULL,
                         `name` varchar(255) DEFAULT NULL,
                         `reference` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `model_colors`
--

CREATE TABLE `model_colors` (
                                `model_id` bigint(20) NOT NULL,
                                `color_id` bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `model_image`
--

CREATE TABLE `model_image` (
                               `id` bigint(20) NOT NULL,
                               `image_path` varchar(255) DEFAULT NULL,
                               `name` varchar(255) DEFAULT NULL,
                               `type` varchar(255) DEFAULT NULL,
                               `model_id` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `model_sizes`
--

CREATE TABLE `model_sizes` (
                               `model_id` bigint(20) NOT NULL,
                               `size_id` bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `model_stock_history`
--

CREATE TABLE `model_stock_history` (
                                       `id` bigint(20) NOT NULL,
                                       `date` datetime DEFAULT NULL,
                                       `model_id` bigint(20) DEFAULT NULL,
                                       `model_name` varchar(255) DEFAULT NULL,
                                       `quantity` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `offer`
--

CREATE TABLE `offer` (
                         `id` bigint(20) NOT NULL,
                         `enabled` bit(1) NOT NULL,
                         `name` varchar(255) DEFAULT NULL,
                         `price` double DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `offer_fb_pages`
--

CREATE TABLE `offer_fb_pages` (
                                  `offer_id` bigint(20) NOT NULL,
                                  `fb_page_id` bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `offer_model`
--

CREATE TABLE `offer_model` (
                               `model_id` bigint(20) NOT NULL,
                               `offer_id` bigint(20) NOT NULL,
                               `quantity` int(11) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `packet`
--

CREATE TABLE `packet` (
                          `id` bigint(20) NOT NULL,
                          `address` varchar(255) DEFAULT NULL,
                          `barcode` varchar(255) DEFAULT NULL,
                          `confirmation_date` datetime DEFAULT NULL,
                          `customer_name` varchar(255) DEFAULT NULL,
                          `customer_phone_nb` varchar(255) DEFAULT NULL,
                          `date` datetime DEFAULT NULL,
                          `delivery_price` double DEFAULT NULL,
                          `discount` double NOT NULL,
                          `exchange` bit(1) NOT NULL,
                          `last_delivery_status` varchar(255) DEFAULT NULL,
                          `last_update_date` datetime DEFAULT NULL,
                          `old_client` int(11) DEFAULT NULL,
                          `packet_description` varchar(255) DEFAULT NULL,
                          `packet_reference` varchar(255) DEFAULT NULL,
                          `price` double NOT NULL,
                          `print_link` varchar(255) DEFAULT NULL,
                          `related_products` varchar(255) DEFAULT NULL,
                          `status` varchar(255) DEFAULT NULL,
                          `valid` bit(1) NOT NULL,
                          `city_id` bigint(20) DEFAULT NULL,
                          `fbpage_id` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `packet_details`
--

CREATE TABLE `packet_details` (
                                  `packetId` bigint(20) NOT NULL,
                                  `barcode` varchar(255) DEFAULT NULL,
                                  `colors` varchar(255) DEFAULT NULL,
                                  `modelName` varchar(255) DEFAULT NULL,
                                  `offerName` varchar(255) DEFAULT NULL,
                                  `quantity` int(11) NOT NULL,
                                  `sizes` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `packet_status`
--

CREATE TABLE `packet_status` (
                                 `id` bigint(20) NOT NULL,
                                 `date` datetime DEFAULT NULL,
                                 `status` varchar(255) DEFAULT NULL,
                                 `packet_id` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `product`
--

CREATE TABLE `product` (
                           `id` bigint(20) NOT NULL,
                           `date` datetime DEFAULT NULL,
                           `quantity` int(11) NOT NULL,
                           `reference` varchar(255) DEFAULT NULL,
                           `color_id` bigint(20) DEFAULT NULL,
                           `model_id` bigint(20) DEFAULT NULL,
                           `size_id` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `products_packet`
--

CREATE TABLE `products_packet` (
                                   `id` bigint(20) NOT NULL,
                                   `packet_date` datetime DEFAULT NULL,
                                   `packet_offer_id` int(11) DEFAULT NULL,
                                   `status` int(11) DEFAULT NULL,
                                   `offer_id` bigint(20) DEFAULT NULL,
                                   `packet_id` bigint(20) DEFAULT NULL,
                                   `product_id` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `product_history`
--

CREATE TABLE `product_history` (
                                   `id` bigint(20) NOT NULL,
                                   `last_modification_date` datetime DEFAULT NULL,
                                   `product_id` bigint(20) DEFAULT NULL,
                                   `quantity` int(11) NOT NULL,
                                   `reference` varchar(255) DEFAULT NULL,
                                   `model_id` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `roles`
--

CREATE TABLE `roles` (
                         `role_id` int(11) NOT NULL,
                         `description` varchar(255) DEFAULT NULL,
                         `name` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `size`
--

CREATE TABLE `size` (
                        `id` bigint(20) NOT NULL,
                        `description` varchar(255) DEFAULT NULL,
                        `reference` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `user`
--

CREATE TABLE `user` (
                        `user_id` int(11) NOT NULL,
                        `enabled` bit(1) DEFAULT NULL,
                        `full_name` varchar(255) NOT NULL,
                        `password` varchar(255) NOT NULL,
                        `user_name` varchar(255) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `users_roles`
--

CREATE TABLE `users_roles` (
                               `user_id` int(11) NOT NULL,
                               `role_id` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `city`
--
ALTER TABLE `city`
    ADD PRIMARY KEY (`id`),
  ADD KEY `FKns6mlanom5wi3wkkpt6ona99g` (`governorate_id`);

--
-- Index pour la table `color`
--
ALTER TABLE `color`
    ADD PRIMARY KEY (`id`);

--
-- Index pour la table `fb_page`
--
ALTER TABLE `fb_page`
    ADD PRIMARY KEY (`id`);

--
-- Index pour la table `governorate`
--
ALTER TABLE `governorate`
    ADD PRIMARY KEY (`id`);

--
-- Index pour la table `model`
--
ALTER TABLE `model`
    ADD PRIMARY KEY (`id`);

--
-- Index pour la table `model_colors`
--
ALTER TABLE `model_colors`
    ADD KEY `FKc8q6r3cee3f1mm5qd5evnojk2` (`color_id`),
  ADD KEY `FK2si729jwxhpki9cbj5waxj84s` (`model_id`);

--
-- Index pour la table `model_image`
--
ALTER TABLE `model_image`
    ADD PRIMARY KEY (`id`),
  ADD KEY `FK9vr86vukjh2g0owk6h4d8ix9d` (`model_id`);

--
-- Index pour la table `model_sizes`
--
ALTER TABLE `model_sizes`
    ADD KEY `FKedvroggob922tbrtec3vxt8qm` (`size_id`),
  ADD KEY `FKowbw6igigpiyds8dn19kovm89` (`model_id`);

--
-- Index pour la table `model_stock_history`
--
ALTER TABLE `model_stock_history`
    ADD PRIMARY KEY (`id`);

--
-- Index pour la table `offer`
--
ALTER TABLE `offer`
    ADD PRIMARY KEY (`id`);

--
-- Index pour la table `offer_fb_pages`
--
ALTER TABLE `offer_fb_pages`
    ADD PRIMARY KEY (`offer_id`,`fb_page_id`),
  ADD KEY `FK34y6sg2hdvxmwo9ek4wg13sod` (`fb_page_id`);

--
-- Index pour la table `offer_model`
--
ALTER TABLE `offer_model`
    ADD PRIMARY KEY (`model_id`,`offer_id`),
  ADD KEY `FKtq51vy680ay66lrpnfb63faib` (`offer_id`);

--
-- Index pour la table `packet`
--
ALTER TABLE `packet`
    ADD PRIMARY KEY (`id`),
  ADD KEY `idx_customer_phone_nb` (`customer_phone_nb`(250)),
  ADD KEY `idx_barcode` (`barcode`(250)),
  ADD KEY `FK51khenlrt8kmf6r7a3w6cmknt` (`city_id`),
  ADD KEY `FKlh9xgjgw3j60rjdy63fi5265` (`fbpage_id`);

--
-- Index pour la table `packet_details`
--
ALTER TABLE `packet_details`
    ADD PRIMARY KEY (`packetId`);

--
-- Index pour la table `packet_status`
--
ALTER TABLE `packet_status`
    ADD PRIMARY KEY (`id`),
  ADD KEY `FKrjeu6vfoo1cj91gx8v8w900it` (`packet_id`);

--
-- Index pour la table `product`
--
ALTER TABLE `product`
    ADD PRIMARY KEY (`id`),
  ADD KEY `FKran04qb31bnslgemxkxexe90p` (`color_id`),
  ADD KEY `FK64vvw59e9kdq1n4tb7w73t4c6` (`model_id`),
  ADD KEY `FKauht6qap8agsu4txxtfsqm5l3` (`size_id`);

--
-- Index pour la table `products_packet`
--
ALTER TABLE `products_packet`
    ADD PRIMARY KEY (`id`),
  ADD KEY `FKq20skd38imdmjwvt2oaeytvr3` (`offer_id`),
  ADD KEY `FKoj0q0hudplpqalqe44c9lpqen` (`packet_id`),
  ADD KEY `FK92fci8nok0yfo4n9ar1o84qvt` (`product_id`);

--
-- Index pour la table `product_history`
--
ALTER TABLE `product_history`
    ADD PRIMARY KEY (`id`),
  ADD KEY `FK5qji1b2l4db0mkiy5t43snq5i` (`model_id`);

--
-- Index pour la table `roles`
--
ALTER TABLE `roles`
    ADD PRIMARY KEY (`role_id`);

--
-- Index pour la table `size`
--
ALTER TABLE `size`
    ADD PRIMARY KEY (`id`);

--
-- Index pour la table `user`
--
ALTER TABLE `user`
    ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `UK_lqjrcobrh9jc8wpcar64q1bfh` (`user_name`) USING HASH;

--
-- Index pour la table `users_roles`
--
ALTER TABLE `users_roles`
    ADD PRIMARY KEY (`user_id`,`role_id`),
  ADD KEY `FKj6m8fwv7oqv74fcehir1a9ffy` (`role_id`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `city`
--
ALTER TABLE `city`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `color`
--
ALTER TABLE `color`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `fb_page`
--
ALTER TABLE `fb_page`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `governorate`
--
ALTER TABLE `governorate`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `model`
--
ALTER TABLE `model`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `model_image`
--
ALTER TABLE `model_image`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `model_stock_history`
--
ALTER TABLE `model_stock_history`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `offer`
--
ALTER TABLE `offer`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `packet`
--
ALTER TABLE `packet`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `packet_status`
--
ALTER TABLE `packet_status`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `product`
--
ALTER TABLE `product`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `products_packet`
--
ALTER TABLE `products_packet`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `product_history`
--
ALTER TABLE `product_history`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `roles`
--
ALTER TABLE `roles`
    MODIFY `role_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `size`
--
ALTER TABLE `size`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `user`
--
ALTER TABLE `user`
    MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

--
-- Add default roles (ROLE_ADMIN, ROLE_USER, ROLE_PROVIDER)
--
INSERT INTO `roles`(`name`, `description`) VALUES ('ROLE_ADMIN', 'Administrateur');
INSERT INTO `roles`(`name`, `description`) VALUES ('ROLE_USER', 'Utilisateur');
INSERT INTO `roles`(`name`, `description`) VALUES ('ROLE_PROVIDER', 'Fournisseur');

--
-- Add default user
--
INSERT INTO `user`(`full_name`, `user_name`, `password`, `enabled`) VALUES ('admin','admin','$2y$10$zZZiwU3nWCVqva8c41Xcsu3Uvs87/jUaIHUiE909/NFp8VZ2uNmJO',1);
INSERT INTO `users_roles`(`user_id`, `role_id`) VALUES (1,1);