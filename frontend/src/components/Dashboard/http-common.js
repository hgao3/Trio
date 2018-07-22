import axios from 'axios'
import * as firebase from 'firebase'

export const AXIOS = axios.create({
  baseURL: `/rest`
})

let ApiWrapper = (function () {

  function Task(obj) {

    const patch_object = {title: "", assigned_user_email: "", stage_id: 0, content: "", due_date: ""};

    let local_object = {

      task_id: obj.task_id || null,
      title: obj.title || '',
      due_date: obj.due_date|| '',
      content: obj.content || '',
      stage_id: obj.stage_id || null,
      assigned_user_email: obj.assigned_user_email || '',
      ready_for_review: obj.ready_for_review || false,
      completed: obj.completed || false,
      _waiting_for_data: true,

      getID() {
        return this.task_id;
      },

      getTitle() {
        return this.title;
      },

      getContent() {
        return this.content;
      },

      getDueDate() {
        return Date.parse(this.due_date);
      },

      getCreateDate() {
        return Date.parse(this.create_date);
      },

      getUpdateDate() {
        return Date.parse(this.update_date);
      },

      isReadyForReview() {
        return this.ready_for_review;
      },

      isCompleted() {
        return this.completed;
      },

      setTitle(string) {
        this.title = string;
        const object = Object.assign(patch_object, {title: string});
        AXIOS.patch(`task/${this.task_id}`, object, {headers: {idToken: id_token}});
      },

      setContent(string) {
        this.content = string;
        const object = Object.assign(patch_object, {content: string});
        AXIOS.patch(`task/${this.task_id}`, object, {headers: {idToken: id_token}});
      },

      setDueDate(date) {
        this.due_date = date;
        let formatted_date = new Date(date).toISOString().split('T')[0];
        const object = Object.assign(patch_object, {due_date: formatted_date});
        AXIOS.patch(`task/${this.task_id}`, object, {headers: {idToken: id_token}});
      },

      isOverdue() {
        return Date.parse(this.due_date) <= new Date();
      },

      setStage(stage_id) {
        this.stage_id = stage_id;
        const object = Object.assign(patch_object, {stage_id: stage_id});
        AXIOS.patch(`task/${this.task_id}`, object, {headers: {idToken: id_token}});
      },

      getStage() {
        return this.stage_id;
      },

      markReady() {
        this.ready_for_review = true;
        AXIOS.patch(`task/${this.task_id}/mark_ready`, {}, {headers: {idToken: id_token}});
      },

      markNotReady() {
        this.ready_for_review = false;
        AXIOS.patch(`task/${this.task_id}/mark_not_ready`, {}, {headers: {idToken: id_token}});
      },

      markCompleted() {
        this.completed = true;
        AXIOS.patch(`task/${this.task_id}/mark_completed`, {}, {headers: {idToken: id_token}});
      },

      markIncomplete() {
        this.completed = false;
        AXIOS.patch(`task/${this.task_id}/mark_incomplete`, {}, {headers: {idToken: id_token}});
      }


    };

    if (local_object.task_id !== null) {
      AXIOS.get(`task/${local_object.task_id}`, {headers: {idToken: id_token}}).then( response => {
        let data = response.data;
        local_object.title = data.title;
        local_object.due_date = data.due_date;
        local_object.content = data.content;
        local_object.stage_id = data.stage_id;
        local_object.assigned_user_email = data.assigned_user_email;
        local_object.ready_for_review = data.ready_for_review;
        local_object.completed = data.completed;
        local_object._waiting_for_data = false;
      })
    }
    else {
      let post_data = {
        title: local_object.title,
        due_date: local_object.due_date,
        content: local_object.content,
        stage_id: local_object.stage_id,
        assigned_user_email: local_object.assigned_user_email
      };
      let request_config = {
        headers: {idToken: id_token}
      };
      AXIOS.post(`task`, post_data, request_config)
        .then(response => {
          local_object.task_id = response.data;
          local_object._waiting_for_data = false;
        })
        .catch(response => { console.log(response); });
    }

    return local_object;
  }

 function Stage(obj) {

    let local_object = {

      stage_id: obj.stage_id || null,
      title: obj.title || '',
      tasks: obj.tasks || [],
      project_id: obj.project_id || null,
      _waiting_for_data: true,

      getID() {
        return this.stage_id;
      },

      getTitle() {
        return this.title;
      },

      setTitle(string) {
        this.title = string;
      },

      getTasks() {
        let task_objects = [];
        this.tasks.forEach(function (task_id) {
          let task = ApiWrapper.getTask(task_id);
          task_objects.push(task);
        });
        return task_objects;
      },

      insertTask(task) {
        if (this.tasks.indexOf(task.getID()) === -1) {
          this.tasks.push(task.getID());
          task.setStage(this.stage_id);
        }
      },

      removeTask(task) {
        let index = this.tasks.indexOf(task.getID());
        if (index !== -1) {
          this.tasks.splice(index, 1);
        }
        /*if (task.getStage() === this.project_id) {
          task.setStage(null);
        }*/

      }

    };

    if (local_object.stage_id !== null) {
      AXIOS.get(`/stage/${local_object.stage_id}`,
        {headers: {
          'idToken': id_token}
        }).then(response => {
          let data = response.data;
          local_object.stage_id = data.stage_id;
          local_object.title = data.title;
          local_object.tasks = data.tasks;
          local_object.project_id = data.project_id;
          local_object._waiting_for_data = false;
      });

    }
    else {
      let config = {headers: {'idToken': id_token}};
      let post_data = { title: local_object.title, project_id: local_object.project_id};
      AXIOS.post(`stages`, post_data, config).then( response => {
        let data = response.data;
        local_object.id = data.id;
        local_object._waiting_for_data = false;
      })
    }

    return local_object;
  }

  let id_token = '';

  return {

    setIdToken(string) {
      id_token = string;
    },

    getUser(email) {
      let user = {
        email: '',
        firstname: '',
        lastname: '',
        uid: '',
        _waiting_for_data: true
      };
      AXIOS.get(`/user/email/${email}`, {headers: {idToken: id_token}})
        .then( function(response) {
          let uid = response.data.uid;
          firebase.database().ref('users').child(uid)
            .on('value', snapshot => {
              let payload = snapshot.val();
              user.email = payload.email;
              user.firstname = payload.firstname;
              user.lastname = payload.lastname;
              user.uid = uid;
              user._waiting_for_data = false;
            })
        } )
        .catch( response => { console.log(response); })
      return user;
    },

    getUsers() {
      let users = [];
      AXIOS.get('/user').then(function(user_records) {
        user_records.data.forEach(function(record) {
          users.push(new User(record));
        })
      });
      return users;
    },

    getStage(id) {
      return new Stage({stage_id: id});
    },

    getTask(id) {
      return new Task({task_id: id});
    },

    async postTask(new_title, new_content, new_due_date, assigned_user_email, stage_id) {
      let constructor_object = {
        title: new_title,
        content: new_content,
        due_date: new_due_date,
        assigned_user_email: assigned_user_email,
        stage_id: stage_id/*,
        task_id: null*/
      };
      let postConfig =  {headers: {idToken: id_token}};
      let response =  await AXIOS.post(`/task`, constructor_object, postConfig);
      return response.data;

    },

    postStage(new_title, new_tasks) {
      let constructor_object = {
        stage_id: null,
        title: new_title,
        tasks: new_tasks
      };
      return new Stage(constructor_object);
    }
  }
})();

export {ApiWrapper};
