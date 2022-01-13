<?php
	if ($_SERVER['REQUEST_METHOD'] === 'POST') {
		include 'connection.php';

		$whichFragment = $_POST['whichFragment'];
		$user_id = $_POST['userid'];

		// Query to retrieve chats.
		$sql = "SELECT chats.chatid, users.userid, users.name, users.profile_image, ";
		$sql .= "COALESCE(messages.message, '') AS message, ";
		$sql .= "COALESCE(messages.dateTime, '') AS dateTime, ";
		$sql .= "IF(messages.senderid<>:user_id AND messages.isMsgSeen=false, 1, 0) AS isNewMsg, ";
		$sql .= "'no' AS isGroup ";
		$sql .= "FROM chats ";
		$sql .= "INNER JOIN users ";
		$sql .= "ON (CASE WHEN chats.user1id=:user_id THEN chats.user2id WHEN chats.user2id=:user_id THEN chats.user1id END) = users.userid ";
		$sql .= "LEFT JOIN (SELECT chatid, max(dateTime) AS mDateTime FROM messages GROUP BY chatid) AS maxDateTime ";
		$sql .= "ON chats.chatid = maxDateTime.chatid ";
		$sql .= "LEFT JOIN messages ";
		$sql .= "ON maxDateTime.chatid = messages.chatid AND maxDateTime.mDateTime = messages.dateTime ";
		$sql .= "WHERE ";

		if($whichFragment === "chats") {
			$sql .= "chats.spammer=-1 OR chats.spammer=:user_id ";
		
			// Query to retrieve groups.
			$sql .= "UNION ";
			$sql .= "SELECT groups.chatid, '' AS userid, groups.name, null AS profile_image, ";
			$sql .= "COALESCE(messages.message, '') AS message, ";
			$sql .= "COALESCE(messages.dateTime, '') AS dateTime, ";
			$sql .= "IF(group_members.lastRetrievalDateTime < messages.dateTime, 1, 0) AS isNewMsg, ";
			$sql .= "'yes' AS isGroup ";
			$sql .= "FROM group_members ";
			$sql .= "INNER JOIN groups ";
			$sql .= "ON group_members.userid=:user_id AND group_members.chatid=groups.chatid ";
			$sql .= "LEFT JOIN (SELECT chatid, max(dateTime) AS mDateTime FROM messages GROUP BY chatid) AS maxDateTime ";
			$sql .= "ON groups.chatid = maxDateTime.chatid ";
			$sql .= "LEFT JOIN messages ";
			$sql .= "ON maxDateTime.chatid = messages.chatid AND maxDateTime.mDateTime = messages.dateTime ";
		}

		else
			$sql .= "chats.spammer=0 OR (chats.spammer<>-1 AND chats.spammer<>:user_id) ";
		
		$sql .= "ORDER BY dateTime DESC";

		$stmt = $conn->prepare($sql);
		$stmt->execute(array("user_id" => $user_id));

		$res = array();
		while($row = $stmt->fetch(PDO::FETCH_ASSOC))
			array_push($res, $row);
		
		header('Content-Type: application/json');
		echo json_encode($res);
	}
?>
