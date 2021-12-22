<?php
	if ($_SERVER['REQUEST_METHOD'] === 'POST') {
		include 'connection.php';

		$phone_nos = $_POST['phone_nos'];
		$user_id = $_POST['userid'];

		$phone_nos = str_replace("]", ")", str_replace("[", "(", $phone_nos));

		$sql = "SELECT users.userid, users.name, users.profile_image, chats.chatid ";
		$sql .= "FROM (SELECT userid, name, profile_image FROM users WHERE mobile_no IN $phone_nos) AS users ";
		$sql .= "LEFT OUTER JOIN chats ";
		$sql .= "ON (users.userid=chats.user1id AND :user_id=chats.user2id) OR (users.userid=chats.user2id AND :user_id=chats.user1id) ";
		$sql .= "ORDER BY users.name";
		
		$stmt = $conn->prepare($sql);
		$stmt->execute(array("user_id" => $user_id));

		$res = array();
		while($row = $stmt->fetch(PDO::FETCH_ASSOC))
			array_push($res, $row);
		
		header('Content-Type: application/json');
		echo json_encode($res);
	}
?>
