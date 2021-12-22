<?php
	include 'base.php';
	
	$servername = SERVER_NAME;
	$dbname = "helloworld";
	$username = "root";
	$password = "";

	$conn = new PDO("mysql: host=$servername; dbname=$dbname", $username, $password);
	$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
?>
