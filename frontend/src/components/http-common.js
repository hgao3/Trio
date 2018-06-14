import axios from 'axios'

export const AXIOS = axios.create({
  baseURL: `/api`
})

let ApiWrapper = (function () {

  let StagesTable = [
    {id: 0, title: 'Backlog', tasks: [0, 1]},
    {id: 1, title: 'To Do', tasks: [2, 3, 4]},
    {id: 2, title: 'In Progress', tasks: [5, 6]},
    {id: 3, title: 'QA', tasks: [7, 8]},
    {id: 4, title: 'UAT', tasks: [9]}
  ];

  let TasksTable = [
    {id: 0, title: 'practice conference presentation', content: 'for AwesomeCon 2018', due_date: '2018-06-01'},
    {
      id: 1,
      title: 'place job ad',
      content: "we need a new Scrum Master since Tim's unfortunate accident",
      due_date: '2018-06-11'
    },
    {
      id: 2,
      title: 'call back angel investor',
      content: "let him know everything will be done on time",
      due_date: '2018-07-02'
    },
    {id: 3, title: 'refill beer keg', content: "check if they have any good seasonal brews", due_date: '2018-07-02'},
    {
      id: 4,
      title: 'make API more RESTful',
      content: "it's very sleepy",
      due_date: '2018-07-02'
    },
    {
      id: 5,
      title: 'implement user  geo-tracking',
      content: 'we had complaints about too much privacy',
      due_date: '2018-07-03'
    },
    {
      id: 6,
      title: 'add filtering to TPS reports',
      content: 'by date, by author, by level of utter irrelevance',
      due_date: '2018-07-03'
    },
    {id: 7, title: 'revise UI', content: 'we look too much like our competitors', due_date: '2017-07-03'},
    {id: 8, title: 'order for cookout', content: 'everyone likes hamburgers', due_date: '2018-07-03'},
    {
      id: 9,
      title: 'eat your own dog food',
      content: 'otherwise known as building the bridge while you cross it',
      due_date: '2018-07-05'
    }
  ];

  function Task(obj) {
    let record = (function () {
      let id;
      if (typeof(obj.id) === 'undefined') {
        id = null;
      }
      else {
        id = obj.id;
      }
      let title = obj.title || '';
      let content = obj.content || '';
      let due_date = obj.due_date || '';

      if (id === null) {
        id = TasksTable.length;
        TasksTable[id] = {id: id, title: title, content: content, due_date: due_date};
      }

      return TasksTable[id];
    })();

    return {

      id: record.id,
      title: record.title,
      due_date: record.due_date === '' ? null : new Date(record.due_date),
      content: record.content,

      getID() {
        return record.id;
      },

      getTitle() {
        return record.title;
      },

      getContent() {
        return record.content;
      },

      getDueDate() {
        return Date.parse(record.due_date);
      },

      setTitle(string) {
        this.title = record.title = string;
      },

      setContent(string) {
        this.content = record.content = string;
      },

      setDueDate(date) {
        record.due_date = new Date(date).toISOString().split('T')[0];
        this.due_date = record.due_date === '' ? null : new Date(record.due_date);
      },

      isOverdue() {
        return Date.parse(record.due_date) <= new Date();
      }

    };
  }

  function Stage(obj) {

    let record = (function () {
      let id;
      if (typeof(obj.id) === 'undefined') {
        id = null;
      }
      else {
        id = obj.id;
      }
      let title = obj.title || '';
      let tasks = obj.tasks || [];

      if (id === null) {
        id = StagesTable.length;
        StagesTable[id] = {id: id, title: title, tasks: tasks};
      }

      return StagesTable[id];
    })();

    return {

      getID() {
        return record.id;
      },

      getTitle() {
        return record.title;
      },

      setTitle(string) {
        this.title = record.title = string;
      },

      getTasks() {
        let task_objects = [];
        record.tasks.forEach(function (task_id) {
          if (task_id in TasksTable) {
            let task_record = TasksTable[task_id];
            let task_object = new Task(task_record);
            task_objects.push(task_object);
          }
        });
        return task_objects;
      },

      insertTask(task) {
        record.tasks.push(task.getID());
        this.tasks.push(task);
      },

      removeTask(task) {
        let index = record.tasks.indexOf(task.id);
        if (index !== -1) {
          record.tasks.splice(index, 1);
          this.tasks.splice(index, 1);
        }

      },

      id: record.id,
      title: record.title,
      tasks: record.tasks.map(function(task_id) { return new Task(TasksTable[task_id]); } )

    };
  }

  return {
    getStage(id) {
      return new Stage(StagesTable[id]);
    },

    getStages() {
      return StagesTable.map(function (stage_record) {
        return new Stage(stage_record);
      });
    },

    getTask(id) {
      return new Task(TasksTable[id]);
    },

    getTasks() {
      return TasksTable.map(function (task_record) {
        return new Task(task_record);
      });
    },

    postTask(new_title, new_content, new_due_date) {
      let constructor_object = {
        title: new_title,
        content: new_content,
        due_date: new_due_date,
        id: null
      };
      return new Task(constructor_object);

    },

    postStage(new_title, new_tasks) {
      let constructor_object = {
        id: null,
        title: new_title,
        tasks: new_tasks
      };
      return new Stage(constructor_object);
    }
  }
})();

export {ApiWrapper};
