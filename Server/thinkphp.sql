-- phpMyAdmin SQL Dump
-- version 4.1.14
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: 2015-05-29 13:52:19
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
-- 表的结构 `think_score`
--

CREATE TABLE IF NOT EXISTS `think_score` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `player1` varchar(20) NOT NULL,
  `player2` varchar(20) NOT NULL,
  `player3` varchar(20) NOT NULL,
  `player4` varchar(20) NOT NULL,
  `score` int(11) NOT NULL,
  `matchtime` varchar(19) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=32 ;

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
