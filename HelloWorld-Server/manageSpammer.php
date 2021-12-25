<?php
	if ($_SERVER['REQUEST_METHOD'] === 'POST') {
		include 'connection.php';

		// State user as spammer.
		if($_POST['whatToDo'] === "stateAsSpammer") {
			$chatid = $_POST['chatid'];
			$myuserid = $_POST['myuserid'];
			$userid = $_POST['userid'];

			if($userid !== $myuserid) {
				$stmt1 = $conn->prepare("SELECT spammer FROM chats WHERE chatid=:chatid");
				$stmt1->execute(array(":chatid" => $chatid));
				$row1 = $stmt1->fetch(PDO::FETCH_ASSOC);
				$spammer = ($row1['spammer'] == $myuserid || $row1['spammer'] == 0) ? 0 : $userid;
				$stmt2 = $conn->prepare("UPDATE chats SET spammer=:spammer WHERE chatid=:chatid");
				$stmt2->execute(array(":spammer" => $spammer, ":chatid" => $chatid));
			}
			
			echo strval("1");
		}

		// Unstate user as spammer.
		else if($_POST['whatToDo'] === "unstateAsSpammer") {
			$chatid = $_POST['chatid'];
			$myuserid = $_POST['myuserid'];
			$userid = $_POST['userid'];

			if($userid !== $myuserid) {
				$stmt1 = $conn->prepare("SELECT spammer FROM chats WHERE chatid=:chatid");
				$stmt1->execute(array(":chatid" => $chatid));
				$row1 = $stmt1->fetch(PDO::FETCH_ASSOC);
				$spammer = ($row1['spammer'] == $userid || $row1['spammer'] == -1) ? -1 : $myuserid;
				$stmt2 = $conn->prepare("UPDATE chats SET spammer=:spammer WHERE chatid=:chatid");
				$stmt2->execute(array(":spammer" => $spammer, ":chatid" => $chatid));
			}

			echo strval("1");
		}
	}
?>
