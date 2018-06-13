import axios from 'axios'

export const AXIOS = axios.create({
  baseURL: `/api`
})

let fakeAPI = (function(){

  let stages = [
    {title: 'Backlog', tasks: [0, 1]},
    {title: 'To Do', tasks: [2, 3, 4]},
    {title: 'In Progress', tasks: [5, 6]},
    {title: 'QA', tasks: [7, 8]},
    {title: 'UAT', tasks: [9]}
  ];

  let tasks = [
    { title: 'practice conference presentation', content: 'for AwesomeCon 2018', due_date: '2018-06-01' },
    { title: 'put in job ad', content: "we need a new Scrum Master since Tim's unfortunate accident", due_date: '2018-06-11' },
    { title: 'call back Stefan', content: "needs to be assured that project will be done on time", due_date: '2018-07-02' },
    { title: 'refill beer keg', content: "check if they have any good seasonal brews", due_date: '2018-07-02' },
    { title: 'make API edits', content: "Miffy said that we weren't being RESTful enough", due_date: '2018-07-02' },
    { title: 'implement user  geo-tracking', content: 'we had complaints about too much privacy', due_date: '2018-07-03' },
    { title: 'add filtering to TPS reports', content: 'by date, by author, by level of utter irrelevance', due_date: '2018-07-03' },
    { title: 'revise UI', content: 'we look too much like our competitors', due_date: '2017-07-03' },
    { title: 'order for cookout', content: 'everyone likes hamburgers', due_date: '2018-07-03' },
    { title: 'eat your own dog food', content: 'this thing had better do everything Trello can do', due_date: '2018-07-05' }
  ];

  return {
    getStage: function (id) {
      return stages[id];
    },
    getStages: function() {
      return stages;
    },
    getTask: function (id) {
      return tasks[id]
    },
    postTask: function(title, content, due_date) {
      console.log('postTake title = ' + title);
      let task = {};
      task['title'] = title;
      task['content'] = content;
      task['due_date'] = due_date;
      tasks.push(task);
      console.log(tasks);
      return tasks.length - 1;
    },
    postStage: function(title, tasks) {
      let stage = {};
      stage['title'] = title;
      stage['tasks'] = tasks;
      stages.push(stage);
      return tasks.length - 1;
    },
    patchTask: function(id, new_task) {
      tasks[id] = new_task;
    },
    patchStage: function(id, new_stage) {
      stages[id] = new_stage;
    },
    changeTaskStage: function (task_id, stage_id) {
      stages.forEach(function(stage) {
        let index = stage.tasks.indexOf(task_id);
        if (index > -1) {
          stage.tasks.splice(index);
        }
      });
      stages[stage_id].tasks.push(task_id);
    }
  }
})();

export {fakeAPI};
