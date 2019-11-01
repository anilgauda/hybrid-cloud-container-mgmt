-- phpMyAdmin SQL Dump
-- version 4.9.0.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Nov 01, 2019 at 12:35 PM
-- Server version: 10.4.6-MariaDB
-- PHP Version: 7.3.9

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `nci_cad_cms`
--

-- --------------------------------------------------------

--
-- Table structure for table `application`
--

CREATE TABLE `application` (
  `id` bigint(20) NOT NULL,
  `cpu_max` int(11) DEFAULT NULL,
  `mem_max` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `registry_image_url` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `container_deployment`
--

CREATE TABLE `container_deployment` (
  `id` bigint(20) NOT NULL,
  `container_id` varchar(255) DEFAULT NULL,
  `deployed_on` datetime DEFAULT NULL,
  `application_id` bigint(20) NOT NULL,
  `vm_id` bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;


-- --------------------------------------------------------

--
-- Table structure for table `provider`
--

CREATE TABLE `provider` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `role`
--

CREATE TABLE `role` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id` bigint(20) NOT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `role_id` bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `vm`
--

CREATE TABLE `vm` (
  `id` bigint(20) NOT NULL,
  `host` varchar(255) DEFAULT NULL,
  `key_file_name` varchar(255) DEFAULT NULL,
  `last_access` datetime DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `provider_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `application`
--
ALTER TABLE `application`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKldca8xj6lqb3rsqawrowmkqbg` (`user_id`);

--
-- Indexes for table `container_deployment`
--
ALTER TABLE `container_deployment`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_omck8b4anmbm1ag2ve6icppc8` (`vm_id`),
  ADD KEY `FK40951pi7h7brmcrsnnrvqi4h9` (`application_id`);

--
-- Indexes for table `flyway_schema_history`
--
ALTER TABLE `flyway_schema_history`
  ADD PRIMARY KEY (`installed_rank`),
  ADD KEY `flyway_schema_history_s_idx` (`success`);

--
-- Indexes for table `provider`
--
ALTER TABLE `provider`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `role`
--
ALTER TABLE `role`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_qleu8ddawkdltal07p8e6hgva` (`role_id`);

--
-- Indexes for table `vm`
--
ALTER TABLE `vm`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_h7twjdnrbp60v7wmr4qc62u31` (`provider_id`),
  ADD UNIQUE KEY `UK_t0967kdh2nii2afhh6jop0e33` (`user_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;