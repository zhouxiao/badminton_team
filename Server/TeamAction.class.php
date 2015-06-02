<?php
// 本类由系统自动生成，仅供测试用途
class TeamAction extends Action {
	
	 // get all team member
   public function fetchall(){
			$User = D('Team');
			$data = $User->field("id, name, alias, sex, age, created, modified, photo")->select();
		
			echo json_encode($data);
	 }
		
	// add new team member	 
	 public function add(){
		$User = D('Team');
		$user['name'] = $_REQUEST['name'];
		$user['alias'] = $_REQUEST['alias'];
		$user['age'] = intval($_REQUEST['age']);
		$user['sex'] = intval($_REQUEST['sex']);
		$user['photo'] = $_REQUEST['photo'];
		$user['created'] = date('Y-m-d H:i:s');
		
   		 // Decode Image
    		$binary=base64_decode($user['photo']);
   		$user['photo'] = $user['alias'].".png";
   	   		
   		 $file = fopen("Public/img/team/".$user['photo'], 'wb');
    		// Create File
    		fwrite($file, $binary);
    		fclose($file);
		
		$result = $User->add($user);
	
		if ($result){
			$Record = M('Change');
			  $data = $Record->where('id=1')->find();
		  	$toggle = $data['toggle'];
		  	if ($toggle == 0){
				  $toggle = 1;
		  	} else {
				  $toggle = 0;
		  	}
		  				
		 $Change = D('Change');
		 $Change->toggle = $toggle;
		 $Change->where('id=1')->save();
		 $last_updated = $Change->where('id=1')->getField('last_updated');
			
		echo json_encode(array('success'=>true, 'id'=>$result, 'last_updated'=>$last_updated));
		} else {
			echo json_encode(array('msg'=>'Some errors occured.'));
		}
	}
	
	// delete team member 	
	public function delete(){

		$User = D('Team');
		$map['id'] = intval($_REQUEST['id']);	
		$result = $User->where($map)->delete();	
		
		if ($result){
			$Record = M('Change');
		  $data = $Record->where('id=1')->find();
		  $toggle = $data['toggle'];
		  if ($toggle == 0){
			  $toggle = 1;
		  } else {
			  $toggle = 0;
		  }
						
		 $Change = D('Change');
		 $Change->toggle = $toggle;
		 $Change->where('id=1')->save();
		
		 $last_updated = $Change->where('id=1')->getField('last_updated');
			
			echo json_encode(array('success'=>true, 'last_updated'=>$last_updated));
		} else {
			echo json_encode(array('msg'=>'Some errors occured.'));
		}		
	}
	
	// update team member
	public function update(){
		$User = D('Team');
		$map['id'] = intval($_REQUEST['id']);	
		$user['name'] = $_REQUEST['name'];
		$user['alias'] = $_REQUEST['alias'];
		$user['age'] = intval($_REQUEST['age']);
		$user['sex'] = intval($_REQUEST['sex']);
		$user['photo'] = $_REQUEST['photo'];
		 // Decode Image
   		 $binary=base64_decode($user['photo']);
    		$user['photo'] = $user['alias'].".png";
			
		$file = fopen("Public/img/team/".$user['photo'], 'wb');
    		// Create File
    		fwrite($file, $binary);
    		fclose($file);
		
		$result = $User->where($map)->save($user);
		if ($result !== false){
		  $Record = M('Change');
		  $data = $Record->where('id=1')->find();
		  $toggle = $data['toggle'];
		  if ($toggle == 0){
			  $toggle = 1;
		  } else {
			  $toggle = 0;
		  }
						
		 $Change = D('Change');
		 $Change->toggle = $toggle;
		 $Change->where('id=1')->save();
		
		 $last_updated = $Change->where('id=1')->getField('last_updated');
			
			
			echo json_encode(array('success'=>true, 'last_updated'=>$last_updated));
		} else {
			echo json_encode(array('msg'=>'Some errors occured.'));
		}		
	}
	
	// get latest update timestamp of table team
	public function getLastUpdatedTimeStamp(){
			$Record = M('Change');
		  $data = $Record->where('id=1')->find();
		  $last_updated = $data['last_updated'];
			
			echo $last_updated;
	}	
	
	// Save match result 
	public function upLoadMatchResult(){
				
		$Match = D('Match');
		$total = intval($_REQUEST['total']);
		for ($i = 1; $i <= $total; $i++){
			$param = "record" . $i;
			$record = $_REQUEST[$param];
			$output .= $record;
			$record = explode(",", $record);
													
			$records[] = array('matchtime'=>$record[0], 
												'player1'=>intval($record[1]), 
												'player2'=>intval($record[2]), 
												'player3'=>intval($record[3]), 
												'player4'=>intval($record[4]), 
												'score'=>intval($record[5]));
		}
      	
		$result = $Match->addAll($records);
		if ($result) {
				echo json_encode(array('success'=>true, 'result'=>$result));		
		} else {
				echo json_encode(array('msg'=>'Some errors occured.'));
		}
	
		
	}

	// get match result 	
	public function downloadData(){
			
		$Match = M('Match'); 
		$condition = intval($_REQUEST['condition']);
		
		switch ($condition){
			case 1:
			 
				$condition = date('Y-m-d') . ' 00:00:00';
				break;
			case 2:
				$d = strtotime('-1 weeks');
				$condition = date('Y-m-d H:i:s', $d);
				break;
			case 3:
				$d = strtotime('-3 months');
				$condition = date('Y-m-d H:i:s', $d);
				break;
			case 4:
				$d = strtotime('-1 years');
				$condition = date('Y-m-d H:i:s', $d);
				break;
		}
		
		$map['matchtime'] = array('EGT', $condition); 
		$records = $Match->where($map)->select();
		
		
		$total = count($records);
		$array['total'] = $total;
	
		for($i = 1; $i <= $total; $i++){
			$record = $records[$i-1];
			$array['record'.$i] = $record['player1'] . ',' . $record['player2'] . ',' 
														. $record['player3'] . ',' . $record['player4'] . ',' 
														. $record['score'] . ',' . $record['matchtime'];
		}
			echo json_encode($array);		
	}
		
	// fetch photostream from db	
	public function fetchPhoto(){
		$User = D('Team');
		$map['id'] = intval($_REQUEST['id']);
		$binary = $User->where($map)->getField('photobyte');
		$stream = base64_encode($binary);
		
		echo json_encode(array('success'=>true, 'stream'=>$stream));
	}

}