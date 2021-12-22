<?php
	if ($_SERVER['REQUEST_METHOD'] === 'POST') {
		include 'connection.php';
		
		$table_name = $_POST['table_name'];
		unset($_POST['table_name']);

		$cols = "";
		$vals = "";
		foreach ($_POST as $col_name => $value) {
			$cols .= ($col_name . ", ");
			$vals .= (":" . $col_name . ", ");
		}
		$cols = substr($cols, 0, -2);
		$vals = substr($vals, 0, -2);

		$sql = "INSERT INTO $table_name ($cols) VALUES ($vals)";
		$stmt = $conn->prepare($sql);
		$stmt->execute($_POST);

		echo strval($conn->lastInsertId());
	}
?>
