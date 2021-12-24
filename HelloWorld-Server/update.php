<?php
	if ($_SERVER['REQUEST_METHOD'] === 'POST') {
		include 'connection.php';
		
		$sql = "UPDATE " . $_POST['table_name'] . " SET " . $_POST['columns'];

		if (isset($_POST['WHERE']))
			$sql .= (" WHERE " . $_POST['WHERE']);
		
		$stmt = $conn->prepare($sql);
		$stmt->execute();

		echo strval("1");
	}
?>
