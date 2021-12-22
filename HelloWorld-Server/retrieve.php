<?php
	if ($_SERVER['REQUEST_METHOD'] === 'POST') {
		include 'connection.php';
		
		$sql = "SELECT " . $_POST['columns'] . " FROM " . $_POST['table_name'];

		if (isset($_POST['WHERE']))
			$sql .= (" WHERE " . $_POST['WHERE']);
		
		if (isset($_POST['ORDER_BY']))
			$sql .= (" ORDER BY " . $_POST['ORDER_BY']);

		$stmt = $conn->prepare($sql);
		$stmt->execute();

		$res = array();
		while($row = $stmt->fetch(PDO::FETCH_ASSOC))
			array_push($res, $row);
		
		header('Content-Type: application/json');
		echo json_encode($res);
	}
?>
