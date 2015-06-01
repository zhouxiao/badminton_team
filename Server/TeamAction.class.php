<?php
// 本类由系统自动生成，仅供测试用途
class TeamAction extends Action {
	
    public function index(){
		$User = D('Team');
		
		$map['id'] = array('between', array('0', '8'));
		$data = $User->where($map)->select();
		
	//	echo $data[0]['name'].' '.$data[0]['alias'].' '.$data[0]['age'].' '.$data[0]['sex'];	
		$this->assign(data, $data);
		
	  $this->display();
    }
	
	 // get all team member
   public function fetchall(){
			$User = D('Team');
			$data = $User->select();
		
			$this->ajaxReturn($data, "JSON");
	 }
		
	// add new team member	 
	 public function add(){
		$User = D('Team');
		$user['name'] = $this->_post('name');
		$user['alias'] = $this->_post('alias');
		$user['age'] = intval($this->_post('age'));
		$user['sex'] = intval($this->_post('sex'));
		$user['photo'] = $this->_post('photo');
		
    		// Decode Image
    		$binary=base64_decode($user['photo']);
   		$user['photo'] = $user['alias'].".png";
		
    		$file = fopen("Public\\img\\team\\".$user['photo'], 'wb');
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
		$map['id'] = intval($this->_post('id'));	
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
		$map['id'] = intval($this->_post('id'));
		$user['name'] = $this->_post('name');
		$user['alias'] = $this->_post('alias');
		$user['age'] = intval($this->_post('age'));
		$user['sex'] = intval($this->_post('sex'));
			
		$user['photo'] = $this->_post('photo');
		 // Decode Image
    $binary=base64_decode($user['photo']);
    $user['photo'] = $user['alias'].".png";
				
    $file = fopen("Public\\img\\team\\".$user['photo'], 'wb');
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
		$total = intval($this->_post('total'));
		for ($i = 1; $i <= $total; $i++){
			$record = $this->_post((string)$i);
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
		
		//$this->ajaxReturn($records, 'JSON');	
	}

	// get match result 	
	public function downloadData(){
		/*
		$User = M('Team');
		$users = $User->getField('id, alias, name');
		*/
		
		$Match = M('Match'); 
		$condition = intval($this->_post('condition'));
		
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
		

}