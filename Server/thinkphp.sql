-- phpMyAdmin SQL Dump
-- version 4.1.14
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: 2015-06-01 08:32:21
-- 服务器版本： 5.6.17
-- PHP Version: 5.5.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `thinkphp`
--

-- --------------------------------------------------------

--
-- 表的结构 `think_change`
--

CREATE TABLE IF NOT EXISTS `think_change` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `last_updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `toggle` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- 转存表中的数据 `think_change`
--

INSERT INTO `think_change` (`id`, `last_updated`, `toggle`) VALUES
(1, '2015-05-29 07:19:53', 0);

-- --------------------------------------------------------

--
-- 表的结构 `think_match`
--

CREATE TABLE IF NOT EXISTS `think_match` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `player1` int(10) unsigned NOT NULL,
  `player2` int(10) unsigned NOT NULL,
  `player3` int(10) unsigned NOT NULL,
  `player4` int(10) unsigned NOT NULL,
  `score` int(11) NOT NULL,
  `matchtime` varchar(19) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=43 ;

--
-- 转存表中的数据 `think_match`
--

INSERT INTO `think_match` (`id`, `player1`, `player2`, `player3`, `player4`, `score`, `matchtime`) VALUES
(22, 17, 18, 11, 12, 5, '2015-05-21 19:29:49'),
(23, 20, 21, 7, 10, -2, '2015-05-31 19:30:06'),
(24, 19, 22, 18, 15, 3, '2015-05-31 19:29:22'),
(25, 14, 16, 20, 9, -4, '2015-05-31 19:29:39'),
(26, 19, 17, 18, 15, 10, '2015-02-11 19:30:58'),
(27, 22, 15, 12, 21, -8, '2015-05-31 19:31:36'),
(28, 10, 7, 11, 12, 15, '2015-04-21 19:31:09'),
(29, 8, 9, 20, 10, 5, '2015-05-31 19:31:20'),
(30, 23, 16, 20, 14, -5, '2015-05-31 19:31:51'),
(31, 11, 12, 21, 10, -10, '2015-05-31 19:32:30'),
(32, 7, 9, 8, 20, -5, '2015-05-31 19:32:22'),
(33, 16, 19, 20, 10, 2, '2015-01-22 19:32:49'),
(34, 22, 19, 15, 18, 14, '2015-05-31 19:32:39'),
(35, 23, 18, 15, 16, 6, '2015-06-01 12:03:17'),
(36, 10, 20, 21, 12, -2, '2015-06-01 12:03:25'),
(37, 8, 9, 7, 10, 5, '2015-06-01 12:03:32'),
(38, 9, 11, 19, 8, -15, '2015-06-01 13:46:35'),
(39, 12, 7, 18, 20, 3, '2015-06-01 13:46:51'),
(40, 17, 22, 7, 18, 15, '2015-06-01 14:14:18'),
(41, 11, 14, 21, 8, -5, '2015-06-01 14:13:59'),
(42, 11, 14, 21, 19, 3, '2015-06-01 14:29:03');

-- --------------------------------------------------------

--
-- 表的结构 `think_team`
--

CREATE TABLE IF NOT EXISTS `think_team` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `alias` varchar(3) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `sex` int(11) DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `photo` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=24 ;

--
-- 转存表中的数据 `think_team`
--

INSERT INTO `think_team` (`id`, `name`, `alias`, `age`, `sex`, `created`, `modified`, `photo`) VALUES
(7, '波波', 'BB', 1, 1, '2015-05-26 06:50:40', '2015-05-26 06:50:40', 'bobo.png'),
(8, '陈昱伟', 'YW', 2, 1, '2015-05-26 06:50:40', '2015-05-26 06:50:40', 'cyw.png'),
(9, '钻石', 'ZS', 1, 0, '2015-05-26 06:50:41', '2015-05-26 06:50:41', 'diamond.png'),
(10, '李辉军', 'HJ', 5, 1, '2015-05-26 06:50:41', '2015-05-26 06:50:41', 'li.png'),
(11, '龙明宣', 'MX', 5, 1, '2015-05-26 06:50:41', '2015-05-26 06:50:41', 'laolong.png'),
(12, '罗剑民', 'JM', 3, 1, '2015-05-26 06:50:41', '2015-05-26 06:50:41', 'luo.png'),
(13, '马军龙', 'LM', 5, 1, '2015-05-26 06:50:41', '2015-05-26 06:50:41', 'ma.png'),
(14, '梅小鱼', 'XY', 2, 0, '2015-05-26 06:50:41', '2015-05-28 11:13:05', 'XY.png'),
(15, '陌陌', 'MM', 1, 1, '2015-05-26 06:50:41', '2015-05-26 06:50:41', 'mo.png'),
(16, '司思', 'SS', 1, 0, '2015-05-26 06:50:41', '2015-05-26 06:50:41', 'sisi.png'),
(17, '孙晓光', 'XG', 5, 1, '2015-05-26 06:50:41', '2015-05-26 06:50:41', 'sun.png'),
(18, '王欣', 'WX', 5, 1, '2015-05-26 06:50:41', '2015-05-26 06:50:41', 'wang.png'),
(19, '周霄', 'ZX', 3, 1, '2015-05-26 06:50:41', '2015-05-28 12:25:22', 'ZX.png'),
(20, '小草', 'XC', 1, 0, '2015-05-26 06:50:41', '2015-05-26 06:50:41', 'xiaocao.png'),
(21, '谢军', 'XJ', 3, 1, '2015-05-26 06:50:41', '2015-05-26 06:50:41', 'xie.png'),
(22, '尹衍锋', 'YF', 3, 1, '2015-05-26 06:50:41', '2015-05-26 06:50:41', 'yin.png'),
(23, '平凡', 'PF', 1, 0, '2015-05-26 06:50:41', '2015-05-26 06:50:41', 'pinfan.png');

-- --------------------------------------------------------

--
-- 表的结构 `think_user`
--

CREATE TABLE IF NOT EXISTS `think_user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `firstname` varchar(20) DEFAULT NULL,
  `lastname` varchar(20) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

--
-- 转存表中的数据 `think_user`
--

INSERT INTO `think_user` (`id`, `firstname`, `lastname`, `phone`, `email`) VALUES
(1, 'Xiao', 'Zhou', '13811163495', 'zhouxiao@msn.com'),
(2, 'Ling', 'Zhou', '4258154273', 'lingzhou@gmail.com'),
(3, 'Jun', 'Xie', '13312163495', 'xiejun@msn.com'),
(4, 'Xia', 'Bai', '1391381234', 'xiabai@gmail.com');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
