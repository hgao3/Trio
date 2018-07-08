import axios from 'axios'

export const AXIOS = axios.create({
  baseURL: `/rest`
})

let ApiWrapper = (function () {

  function User(obj) {

    let local_object = {
      uid: obj.uid || null,
      email: obj.email || '',
      first_name: obj.first_name || '',
      last_name: obj.last_name || '',
      is_admin: obj.is_admin || false,
      projects: obj.projects || [],
      projects_managing: obj.projects_managing || [],
      _waiting_for_data: true,

      getUID: function() { return this.uid;},
      apiURL: function() { return `/user/uid/${this.getUID()}`;},
      getEmail: function() { return this.email; },
      getFirstName: function () { return this.first_name; },
      getLastName: function () { return this.last_name; },
      isAdmin: function () { return this.is_admin; },
      getProjects: function () { return this.projects; },
      getManagedProjects: function () { return this.projects_managing; },

      setEmail: function(email) {
        this.email = email;
        AXIOS.patch(this.apiURL(), {email: this.email});
      },

      setFirstName: function (first_name) {
        this.first_name = first_name;
        AXIOS.patch(this.apiURL(), {first_name: first_name});
      },

      setLastName: function(last_name) {
        this.last_name = last_name;
        AXIOS.patch(this.apiURL(), {last_name: last_name});
      },

      setAdmin: function(admin_status) {
        this.is_admin = admin_status;
        AXIOS.patch(this.apiURL(), {is_admin: admin_status});
      }

    };

    if (local_object.uid !== null) {
      AXIOS.get(
        `/user/uid/${local_object.getUID()}`,
        {headers:
            {'idToken': id_token}
        })
        .then( response => {
          let data = response.data;
          local_object.user_id = data.user_id;
          local_object.first_name = data.first_name;
          local_object.last_name = data.last_name;
          local_object.email = data.email;
          local_object.projects_managing = data.projects_managing;
          local_object.projects = data.projects;
          local_object._waiting_for_data = false;
      });
    }
    else {
      AXIOS.post(`/user/`, {
        first_name: obj.first_name || '',
        last_name: obj.last_name || '',
        email: obj.email || '',
        password: obj.password || '',
        headers: { idToken: id_token }
      }).then(response => {
        local_object.user_id = response.data;
        local_object._waiting_for_data = false;
      });
    }

    return local_object;
  }

  function Task(obj) {
    let local_object = {

      task_id: obj.task_id || null,
      title: obj.title || '',
      due_date: obj.due_date === '' ? null : new Date(obj.due_date),
      content: obj.content || '',
      create_date: obj.create_date || '',
      update_date: obj.update_date || '',
      stage_id: obj.stage_id || '',
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

      get createDate() {
        return Date.parse(this.create_date);
      },

      get updateDate() {
        return Date.parse(this.update_date);
      },

      setTitle(string) {
        this.title = string;
        //AXIOS.patch(`task/${this.task_id}`, {title: this.title}, {headers: {idToken: id_token}});
      },

      setContent(string) {
        this.content = string;
        //AXIOS.patch(`task/${this.task_id}`, {content: this.content}, {headers: {idToken: id_token}});
      },

      setDueDate(date) {
        this.due_date = new Date(date).toISOString().split('T')[0];
        //AXIOS.patch(`task/${this.task_id}`, {due_date: this.due_date}, {headers: {idToken: id_token}});
      },

      isOverdue() {
        return Date.parse(this.due_date) <= new Date();
      },

      setStage(stage_id) {
        this.stage_id = stage_id;
        //AXIOS.patch(`task/${this.task_id}`, {stage_id: this.stage_id}, {headers: {idToken: id_token}});
      },

      getStage() {
        return this.stage_id;
      }

    };

    if (local_object.task_id !== null) {
      AXIOS.get(`task/${local_object.task_id}`, {headers: {idToken: id_token}}).then( response => {
        let data = response.data;
        local_object.title = data.title;
        local_object.due_date = data.due_date;
        local_object.content = data.content;
        local_object.create_date = data.create_date;
        local_object.update_date = data.update_date;
        local_object.stage_id = data.stage_id;
        local_object._waiting_for_data = false;
      })
    }
    else {
      let post_data = {
        title: local_object.title,
        due_date: local_object.due_date,
        content: local_object.content,
        create_date: local_object.create_date,
        update_date: local_object.update_date,
        stage_id: local_object.stage_id
      };
      let request_config = {
        headers: {idToken: id_token}
      };
      AXIOS.post(`task`, post_data, request_config).then(response => {
        local_object.task_id = response.data;
        local_object._waiting_for_data = false;
      })
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
        this.tasks.push(task.getID());
        task.setStage(this.stage_id);
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

    getUser(uid) {
      return new User({uid: uid});
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

    postTask(new_title, new_content, new_due_date) {
      let constructor_object = {
        title: new_title,
        content: new_content,
        due_date: new_due_date,
        task_id: null
      };
      return new Task(constructor_object);

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
