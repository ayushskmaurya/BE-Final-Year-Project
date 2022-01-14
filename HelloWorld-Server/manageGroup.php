<?php
	if ($_SERVER['REQUEST_METHOD'] === 'POST') {
		include 'connection.php';
		
		// Retrieving groups.
		if($_POST['whatToDo'] === "retrieveGroups") {
			$user_id = $_POST['userid'];

			$sql = "SELECT groups.chatid, groups.name ";
			$sql .= "FROM group_members ";
			$sql .= "INNER JOIN groups ";
			$sql .= "ON group_members.userid=:user_id AND group_members.chatid=groups.chatid ";
			$sql .= "ORDER BY groups.name";

			$stmt = $conn->prepare($sql);
			$stmt->execute(array("user_id" => $user_id));
	
			$res = array();
			while($row = $stmt->fetch(PDO::FETCH_ASSOC))
				array_push($res, $row);
			
			header('Content-Type: application/json');
			echo json_encode($res);
		}
	}
?>
