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

		// Creating group.
		if($_POST['whatToDo'] === "createGroup") {
			$group_name = $_POST['groupName'];
			$members_id = $_POST['membersId'];

			$stmt1 = $conn->prepare("INSERT INTO chats () VALUES ()");
			$stmt1->execute();
			$chatid = $conn->lastInsertId();

			$sql2 = "INSERT INTO groups (chatid, name) VALUES (:chat_id, :group_name)";
			$stmt2 = $conn->prepare($sql2);
			$stmt2->execute(array("chat_id" => $chatid, "group_name" => $group_name));

			$group_members_id = json_decode($members_id, true);
			foreach ($group_members_id as $member_id) {
				$sql3 = "INSERT INTO group_members (chatid, userid) VALUES (:chat_id, :member_id)";
				$stmt3 = $conn->prepare($sql3);
				$stmt3->execute(array("chat_id" => $chatid, "member_id" => $member_id));
			}

			echo strval("1");
		}
	}
?>
