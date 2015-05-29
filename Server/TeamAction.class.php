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
	
   public function fetchall(){
			
			$User = D('Team');
	  	$map['id'] = array('between', array('0', '8'));

		 // $data = $User->where($map)->select();
			$data = $User->select();
		
			//echo json_encode($data);
		
			$this->ajaxReturn($data, "JSON");
	 }
	 
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
	
	public function getLastUpdatedTimeStamp(){
			$Record = M('Change');
		  $data = $Record->where('id=1')->find();
		  $last_updated = $data['last_updated'];
			
			echo $last_updated;
	}	
}